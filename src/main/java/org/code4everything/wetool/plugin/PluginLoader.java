package org.code4everything.wetool.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import javafx.scene.control.MenuItem;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.WeApplication;
import org.code4everything.wetool.constant.FileConsts;
import org.code4everything.wetool.plugin.support.WePluginSupportable;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.config.WePluginInfo;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import java.io.File;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author pantao
 * @since 2019/9/26
 */
@Slf4j
@UtilityClass
public final class PluginLoader {

    private static final WeConfig CONFIG = WeUtils.getConfig();

    private static final Set<WePlugin> LOADED_PLUGINS = new HashSet<>();

    private static final Map<String, WePlugin> PREPARE_PLUGINS = new HashMap<>();

    public static void loadPlugins() {
        // 加载工作目录下的plugins目录
        File pluginParent = new File(FileConsts.PLUGIN_FOLDER);
        if (pluginParent.exists()) {
            File[] files = pluginParent.listFiles();
            if (ArrayUtil.isNotEmpty(files)) {
                for (File file : files) {
                    preparePlugin(file, true);
                }
            }
        }
        // 加载其他地方的插件
        Set<String> paths = WeUtils.getConfig().getPluginPaths();
        if (CollUtil.isNotEmpty(paths)) {
            paths.forEach(path -> preparePlugin(new File(path), true));
        }
        loadPluginFromPrepared();
    }

    public static void loadPlugins(Collection<File> plugins, final boolean checkDisable) {
        if (CollUtil.isEmpty(plugins)) {
            return;
        }
        plugins.forEach(plugin -> preparePlugin(plugin, checkDisable));
        loadPluginFromPrepared();
    }

    private void preparePlugin(File file, boolean checkDisable) {
        if (file.isFile()) {
            WePluginInfo info;
            try {
                // 包装成 JarFile
                JarFile jar = new JarFile(file);
                // 读取插件信息
                ZipEntry entry = jar.getEntry("plugin.json");
                if (Objects.isNull(entry)) {
                    log.error(StrUtil.format("plugin {} load failed: {}", file.getName(), "plugin.json not found"));
                    return;
                }
                info = JSON.parseObject(IoUtil.read(jar.getInputStream(entry), "utf-8"), WePluginInfo.class);
                if (checkDisable && CONFIG.getPluginDisables().contains(info)) {
                    // 插件被禁止加载
                    log.info("plugin {}-{}-{} disabled", info.getAuthor(), info.getName(), info.getVersion());
                    return;
                }

                WePlugin plugin = new WePlugin(info, file);
                // 检测插件是否已经加载
                if (LOADED_PLUGINS.contains(plugin)) {
                    return;
                }
                replaceIfNewer(plugin);
            } catch (Exception e) {
                FxDialogs.showException("plugin file load failed: " + file.getName(), e);
            }
        }
    }

    public static void registerPlugin(WePluginInfo info, WePluginSupportable supportable) {
        String reqVer = info.getRequireWetoolVersion();
        String errMsg = "plugin %s-%s-%s incompatible: ";
        errMsg = String.format(errMsg, info.getAuthor(), info.getName(), info.getVersion());
        // 检查plugin要求wetool依赖的wetool-plugin-support版本是否符合要求：current>=required
        if (!WeUtils.isRequiredVersion(AppConsts.CURRENT_VERSION, reqVer)) {
            log.error(errMsg + "the lower version {} of wetool is required", reqVer);
            return;
        }
        // 检查wetool要求plugin依赖的wetool-plugin-support版本是否符合要求：required>=lower
        if (!WeUtils.isRequiredVersion(reqVer, AppConsts.LOWER_VERSION)) {
            log.error(errMsg + "version is lower than the required");
            return;
        }
        // 初始化
        if (!supportable.initialize()) {
            log.info("plugin {}-{}-{} initialize failed", info.getAuthor(), info.getName(), info.getVersion());
            return;
        }
        // 注册主界面插件菜单
        MenuItem barMenu = supportable.registerBarMenu();
        if (ObjectUtil.isNotNull(barMenu)) {
            FxUtils.getPluginMenu().getItems().add(barMenu);
        }
        // 注册托盘菜单
        java.awt.MenuItem trayMenu = supportable.registerTrayMenu();
        WeApplication.addIntoPluginMenu(trayMenu);
        log.info("plugin {}-{}-{} loaded", info.getAuthor(), info.getName(), info.getVersion());
        // 注册成功回调
        supportable.registered(info, barMenu, trayMenu);
    }

    private static void replaceIfNewer(WePlugin plugin) {
        String key = plugin.getPluginInfo().getAuthor() + plugin.getPluginInfo().getName();
        if (PREPARE_PLUGINS.containsKey(key)) {
            WePlugin another = PREPARE_PLUGINS.get(key);
            // 当前版本是否大于预加载的版本
            if (WeUtils.isRequiredVersion(plugin.getPluginInfo().getVersion(), another.getPluginInfo().getVersion())) {
                PREPARE_PLUGINS.put(key, plugin);
            }
        } else {
            PREPARE_PLUGINS.put(key, plugin);
        }
    }

    private static void loadPluginFromPrepared() {
        Iterator<Map.Entry<String, WePlugin>> iterator = PREPARE_PLUGINS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WePlugin> entry = iterator.next();
            loadPlugin(entry.getValue());
            iterator.remove();
        }
    }

    private static void loadPlugin(WePlugin plugin) {
        LOADED_PLUGINS.add(plugin);
        try {
            // 加载插件类
            ClassLoader loader = JarClassLoader.loadJarToSystemClassLoader(plugin.getJarFile());
            Class<?> clazz = loader.loadClass(plugin.getPluginInfo().getSupportedClass());
            WePluginSupportable supportable = (WePluginSupportable) clazz.newInstance();
            // 添加插件菜单
            registerPlugin(plugin.getPluginInfo(), supportable);
        } catch (Exception e) {
            FxDialogs.showException("plugin file load failed: " + plugin.getJarFile().getName(), e);
        }
    }
}
