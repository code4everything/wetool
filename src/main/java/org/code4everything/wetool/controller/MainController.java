package org.code4everything.wetool.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.boot.config.BootConfig;
import org.code4everything.wetool.WeApplication;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.WePluginSupportable;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.config.WePluginInfo;
import org.code4everything.wetool.plugin.support.config.WeStart;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class MainController {

    private final ThreadFactory FACTORY = ThreadFactoryBuilder.create().setDaemon(true).build();

    private final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1, FACTORY);

    private final Map<String, Pair<String, String>> TAB_MAP = new HashMap<>(16);

    private final WeConfig config = WeUtils.getConfig();

    @FXML
    public TabPane tabPane;

    @FXML
    public Menu fileMenu;

    @FXML
    public Menu toolMenu;

    @FXML
    public Menu pluginMenu;

    {
        TAB_MAP.put("FileManager", new Pair<>(TitleConsts.FILE_MANAGER, ViewConsts.FILE_MANAGER));
        TAB_MAP.put("JsonParser", new Pair<>(TitleConsts.JSON_PARSER, ViewConsts.JSON_PARSER));
        TAB_MAP.put("RandomGenerator", new Pair<>(TitleConsts.RANDOM_GENERATOR, ViewConsts.RANDOM_GENERATOR));
        TAB_MAP.put("ClipboardHistory", new Pair<>(TitleConsts.CLIPBOARD_HISTORY, ViewConsts.CLIPBOARD_HISTORY));
        TAB_MAP.put("QrCodeGenerator", new Pair<>(TitleConsts.QR_CODE_GENERATOR, ViewConsts.QR_CODE_GENERATOR));
        TAB_MAP.put("CharsetConverter", new Pair<>(TitleConsts.CHARSET_CONVERTER, ViewConsts.CHARSET_CONVERTER));
        TAB_MAP.put("NetworkTool", new Pair<>(TitleConsts.NETWORK_TOOL, ViewConsts.NETWORK_TOOL));
        TAB_MAP.put("NaryConverter", new Pair<>(TitleConsts.NARY_CONVERTER, ViewConsts.NARY_CONVERTER));
    }

    /**
     * 此对象暂时不注册到工厂
     */
    @FXML
    private void initialize() {
        BeanFactory.register(tabPane);
        WeApplication.setMainController(this);
        config.appendClipboardHistory(new Date(), ClipboardUtil.getStr());
        // 监听剪贴板
        EXECUTOR.scheduleWithFixedDelay(this::watchClipboard, 0, 1000, TimeUnit.MILLISECONDS);
        // 加载快速启动选项
        List<WeStart> starts = WeUtils.getConfig().getQuickStarts();
        if (CollUtil.isNotEmpty(starts)) {
            Menu menu = new Menu(TitleConsts.QUICK_START);
            setQuickStartMenu(menu, starts);
            fileMenu.getItems().add(0, new SeparatorMenuItem());
            fileMenu.getItems().add(0, menu);
        }
        // 加载工具选项卡
        loadToolMenus(toolMenu);
        // 加载默认选项卡
        loadTabs();
        // 加载插件
        ThreadUtil.execute(this::loadPlugins);
    }

    private void loadPlugins() {
        File pluginParent = new File(FileUtils.currentWorkDir("plugins"));
        if (!pluginParent.exists()) {
            return;
        }
        File[] files = pluginParent.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                WePluginSupportable plugin;
                WePluginInfo info;
                try {
                    // 包装成 JarFile
                    JarFile jar = new JarFile(file);
                    // 读取插件信息
                    ZipEntry entry = jar.getEntry("plugin.json");
                    if (Objects.isNull(entry)) {
                        log.error(StrUtil.format("plugin {} load failed: {}", file.getName(), "plugin.json not found"));
                        continue;
                    }
                    info = JSON.parseObject(IoUtil.read(jar.getInputStream(entry), "utf-8"), WePluginInfo.class);
                    if (config.getPluginDisables().contains(info)) {
                        // 插件被禁止加载
                        log.info("plugin {}-{}-{} disabled", info.getAuthor(), info.getName(), info.getVersion());
                        continue;
                    }
                    // 加载插件类
                    ClassLoader loader = JarClassLoader.loadJarToSystemClassLoader(file);
                    Class<?> clazz = loader.loadClass(info.getSupportedClass());
                    plugin = (WePluginSupportable) clazz.newInstance();
                    // 添加插件菜单
                    registerPlugin(info, plugin);
                } catch (Exception e) {
                    FxDialogs.showException("plugin file load failed: " + file.getName(), e);
                }
            }
        }
    }

    public void registerPlugin(WePluginInfo info, WePluginSupportable supportable) {
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
            Platform.runLater(() -> pluginMenu.getItems().add(barMenu));
        }
        // 注册托盘菜单
        java.awt.MenuItem trayMenu = supportable.registerTrayMenu();
        WeApplication.addIntoPluginMenu(trayMenu);
        log.info("plugin {}-{}-{} loaded", info.getAuthor(), info.getName(), info.getVersion());
        // 注册成功回调
        supportable.registered(info, barMenu, trayMenu);
    }

    private void loadToolMenus(Menu menu) {
        menu.getItems().forEach(item -> {
            if (item instanceof Menu) {
                loadToolMenus((Menu) item);
            } else if (StrUtil.isNotEmpty(item.getId())) {
                item.setOnAction(e -> openTab(TAB_MAP.get(item.getId())));
            }
        });
    }

    private void setQuickStartMenu(Menu menu, List<WeStart> starts) {
        starts.forEach(start -> {
            if (CollUtil.isEmpty(start.getSubStarts())) {
                // 添加子菜单
                MenuItem item = new MenuItem(start.getAlias());
                item.setOnAction(e -> FxUtils.openFile(start.getLocation()));
                menu.getItems().add(item);
            } else {
                // 添加父级菜单
                Menu subMenu = new Menu(start.getAlias());
                menu.getItems().add(subMenu);
                setQuickStartMenu(subMenu, start.getSubStarts());
            }
        });
    }

    private void watchClipboard() {
        String clipboard;
        String last;
        try {
            // 忽略系统休眠时抛出的异常
            clipboard = ClipboardUtil.getStr();
            last = config.getLastClipboardHistoryItem().getValue();
        } catch (Exception e) {
            if (BootConfig.isDebug()) {
                log.warn(e.getMessage());
            }
            clipboard = last = "";

        }
        if (StrUtil.isEmpty(clipboard) || last.equals(clipboard)) {
            return;
        }
        // 剪贴板发生变化
        String compress = WeUtils.compressString(clipboard);
        log.info("clipboard changed: {}", compress);
        Date date = new Date();
        config.appendClipboardHistory(date, clipboard);
        ClipboardHistoryController controller = BeanFactory.get(ClipboardHistoryController.class);
        if (ObjectUtil.isNotNull(controller)) {
            // 显示到文本框
            final String clip = clipboard;
            Platform.runLater(() -> controller.insert(date, clip));
        }
    }

    private void loadTabs() {
        for (String tabName : config.getInitialize().getTabs().getLoads()) {
            openTab(TAB_MAP.get(tabName));
        }
    }

    private void openTab(Pair<String, String> tabPair) {
        if (Objects.isNull(tabPair)) {
            return;
        }
        Pane box = FxUtils.loadFxml(tabPair.getValue());
        if (Objects.isNull(box)) {
            return;
        }
        // 打开选项卡
        FxUtils.openTab(box, tabPair.getKey());
    }

    public void openFile() {
        FxUtils.chooseFile(file -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openFile(file);
            }
        });
    }

    public void saveFile() {
        FxUtils.saveFile(file -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.saveFile(file);
            }
        });
    }

    public void openMultiFile() {
        FxUtils.chooseFiles(files -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openMultiFiles(files);
            }
        });
    }

    public void openFolder() {
        FxUtils.chooseFolder(folder -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openFolder(folder);
            }
        });
    }

    public void quit() {
        WeUtils.exitSystem();
    }

    public void about() {
        FxDialogs.showInformation(TitleConsts.ABOUT_APP, TipConsts.ABOUT_APP);
    }

    public void closeAllTab() {
        tabPane.getTabs().clear();
    }

    public void openAllTab() {
        TAB_MAP.forEach((k, v) -> openTab(v));
    }

    public void openLog() {
        FxUtils.openFile(StrUtil.join(File.separator, FileUtil.getUserHomePath(), "logs", "wetool", "wetool.log"));
    }

    public void restart() {
        FxUtils.restart();
    }

    public void openConfig() {
        FxUtils.openFile(WeUtils.getConfig().getCurrentPath());
    }


    public void seeJavaInfo() {
        TextArea area = new TextArea(getAllJavaInfos());
        VBox.setVgrow(area, Priority.ALWAYS);
        VBox box = new VBox(area);
        box.setPrefWidth(600);
        box.setPrefHeight(700);
        FxDialogs.showDialog(null, box);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAllJavaInfos() {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaVirtualMachineSpecification信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJvmSpecInfo());

        builder.append("\r\nJavaVirtualMachineImplementation信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJvmInfo());

        builder.append("\r\nJavaSpecification信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaSpecInfo());

        builder.append("\r\nJavaImplementation信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaInfo());

        builder.append("\r\nJava运行时信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getJavaRuntimeInfo());

        builder.append("\r\n系统信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getOsInfo());

        builder.append("\r\n用户信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getUserInfo());

        builder.append("\r\n当前主机网络地址信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getHostInfo());

        builder.append("\r\n运行时信息：\r\n");
        builder.append("=========================================================================================\r\n");
        builder.append(SystemUtil.getRuntimeInfo());

        return builder.toString();
    }

    public void seePluginRepo() {
        FxUtils.openLink(TipConsts.REPO_LINK);
    }
}
