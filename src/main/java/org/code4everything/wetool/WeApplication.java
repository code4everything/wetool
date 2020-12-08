package org.code4everything.wetool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.boot.base.ObjectUtils;
import org.code4everything.boot.base.constant.IntegerConsts;
import org.code4everything.wetool.constant.FileConsts;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.handler.ExitHttpApiHandler;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.config.WeStart;
import org.code4everything.wetool.plugin.support.druid.DruidSource;
import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.EventMode;
import org.code4everything.wetool.plugin.support.event.EventPublisher;
import org.code4everything.wetool.plugin.support.event.message.QuickStartEventMessage;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.http.HttpService;
import org.code4everything.wetool.plugin.support.http.ObjectResp;
import org.code4everything.wetool.plugin.support.listener.WeKeyboardListener;
import org.code4everything.wetool.plugin.support.listener.WeMouseListener;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class WeApplication extends Application {

    private static final Menu PLUGIN_MENU = new Menu(TitleConsts.PLUGIN);

    private static final ThreadFactory FACTORY =
            ThreadFactoryBuilder.create().setDaemon(true).setUncaughtExceptionHandler((t, e) -> log.error(ExceptionUtil.stacktraceToString(e, Integer.MAX_VALUE))).build();

    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(2, FACTORY);

    private Stage stage;

    private boolean isTraySuccess = false;

    public static void main(String[] args) {
        log.info("starting wetool on os: {}", SystemUtil.getOsInfo().getName());
        log.info("default charset: {}", Charset.defaultCharset().name());
        parseConfig();
        initApp();
        launch(args);
    }

    public static void initApp() {
        // 注册事件
        EventCenter.registerEvent(EventCenter.EVENT_QUICK_START_CLICKED, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_CLEAR_FXML_CACHE, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_WETOOL_RESTART, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_WETOOL_EXIT, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_WETOOL_SHOW, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_WETOOL_HIDDEN, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_CLIPBOARD_CHANGED, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_MOUSE_CORNER_TRIGGER, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_100_MS_TIMER, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_KEYBOARD_PRESSED, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_KEYBOARD_RELEASED, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_MOUSE_MOTION, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_MOUSE_RELEASED, EventMode.MULTI_SUB);
        EventCenter.registerEvent(EventCenter.EVENT_MOUSE_PRESSED, EventMode.MULTI_SUB);

        EXECUTOR.scheduleWithFixedDelay(() -> EventCenter.publishEvent(EventCenter.EVENT_100_MS_TIMER,
                DateUtil.date()), 0, 100, TimeUnit.MILLISECONDS);

        // 关闭 jnative 日志
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new WeKeyboardListener());
            WeMouseListener nativeMouseListener = new WeMouseListener();
            GlobalScreen.addNativeMouseMotionListener(nativeMouseListener);
            GlobalScreen.addNativeMouseListener(nativeMouseListener);
        } catch (NativeHookException ex) {
            log.error("register keyboard listener failed: {}", ExceptionUtil.stacktraceToString(ex, Integer.MAX_VALUE));
        }

        FxUtils.listenKeyEvent();
        List<Integer> shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_ENTER);
        FxUtils.registerGlobalShortcuts(shortcuts, FxUtils::toggleStage);

        connectDb();
        exportHttpService();
    }

    private static void exportHttpService() {
        WeUtils.execute(() -> {
            try {
                HttpService.exportHttp("get/wetool/hello", (req, resp, param, body) -> ObjectResp.of(param, body));
                HttpService.exportHttp("get/wetool/exit", new ExitHttpApiHandler());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    private static void connectDb() {
        JSONArray jsonArray = WeUtils.getConfig().getDbConnections();
        if (CollUtil.isEmpty(jsonArray)) {
            return;
        }
        WeUtils.execute(() -> {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Properties properties = new Properties();
                jsonObject.forEach((k, v) -> properties.put(StrUtil.addPrefixIfNot(k, "druid."), v));
                DruidSource.configDataSource(properties);
            }
        });
    }

    public static void addIntoPluginMenu(MenuItem menuItem) {
        if (ObjectUtils.isNotNull(PLUGIN_MENU, menuItem)) {
            PLUGIN_MENU.add(menuItem);
        }
    }

    private static void parseConfig() {
        // 解析正确的配置文件路径
        String path = WeUtils.parsePathByOs("we-config.json");
        if (StrUtil.isEmpty(path)) {
            log.error("wetool start error: config file not found");
            WeUtils.exitSystem();
        }
        log.info("load config file: {}", path);
        // 解析JSON配置
        JSONObject json = JSON.parseObject(FileUtil.readUtf8String(path));
        WeConfig config = json.toJavaObject(WeConfig.class);
        config.setConfigJson(json);
        config.setCurrentPath(path);
        BeanFactory.register(config);
        // 检测空指针
        config.init();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BeanFactory.register(stage);
        if (SystemUtil.getOsInfo().isWindows()) {
            enableTray();
        }

        // 注册并发布秒钟定时器事件
        String eventKey = EventCenter.EVENT_SECONDS_TIMER;
        EventPublisher publisher = EventCenter.registerEvent(eventKey, EventMode.MULTI_SUB).orElse(null);
        Objects.requireNonNull(publisher);
        EXECUTOR.scheduleWithFixedDelay(publisher::publishEvent, 0, 1, TimeUnit.SECONDS);

        // 加载主界面
        Pane root = FxUtils.loadFxml(WeApplication.class, ViewConsts.MAIN, false);
        if (Objects.isNull(root)) {
            FxDialogs.showError(TipConsts.INIT_ERROR);
            WeUtils.exitSystem();
        }
        // 设置标题
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ViewConsts.ICON)));
        stage.setTitle(FinalUtils.getAppTitle());
        // 监听关闭事件
        stage.setOnCloseRequest((WindowEvent event) -> {
            hideStage();
            event.consume();
        });
        // 设置大小
        WeConfig config = WeUtils.getConfig();
        stage.setWidth(config.getInitialize().getWidth());
        stage.setHeight(config.getInitialize().getHeight());
        stage.setFullScreen(config.getInitialize().getFullscreen());

        if (BooleanUtil.isTrue(WeUtils.getConfig().getInitialize().getHide())) {
            hideStage();
        } else {
            stage.show();
        }
        log.info("wetool started");
        // 处理全局异常
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> FxDialogs.showException(TipConsts.APP_EXCEPTION, throwable));
    }

    private void hideStage() {
        if (isTraySuccess) {
            stage.hide();
        } else {
            stage.setIconified(true);
        }
    }

    private void setQuickStartMenu(Menu menu, Set<WeStart> starts) {
        starts.forEach(start -> {
            if (CollUtil.isEmpty(start.getSubStarts())) {
                // 添加子菜单
                MenuItem item = new MenuItem(start.getAlias());
                item.addActionListener(e -> {
                    QuickStartEventMessage message = QuickStartEventMessage.of(start.getLocation());
                    EventCenter.publishEvent(EventCenter.EVENT_QUICK_START_CLICKED, DateUtil.date(), message);
                    FxUtils.openFile(start.getLocation());
                });
                menu.add(item);
            } else {
                // 添加父级菜单
                Menu subMenu = new Menu(start.getAlias());
                menu.add(subMenu);
                setQuickStartMenu(subMenu, start.getSubStarts());
            }
        });
    }

    /**
     * 系统托盘
     */
    private void enableTray() {
        Platform.setImplicitExit(false);
        // 添加托盘邮件菜单
        PopupMenu popupMenu = new PopupMenu();
        // 快捷打开
        Set<WeStart> starts = WeUtils.getConfig().getQuickStarts();
        if (CollUtil.isNotEmpty(starts)) {
            Menu menu = new Menu(TitleConsts.QUICK_START);
            setQuickStartMenu(menu, starts);
            popupMenu.add(menu);
            popupMenu.addSeparator();
        }
        // 插件菜单
        popupMenu.add(PLUGIN_MENU);
        popupMenu.addSeparator();
        // 打开
        Menu menu = new Menu(TitleConsts.OPEN);
        addQuickOpenMenu(menu);
        popupMenu.add(menu);
        popupMenu.addSeparator();
        // 显示
        MenuItem item = new MenuItem(TitleConsts.SHOW);
        item.addActionListener(e -> FxUtils.showStage());
        popupMenu.add(item);
        // 隐藏
        item = new MenuItem(TitleConsts.HIDE);
        item.addActionListener(e -> FxUtils.hideStage());
        popupMenu.add(item);
        // 重启
        popupMenu.addSeparator();
        item = new MenuItem(TitleConsts.RESTART);
        item.addActionListener(e -> FxUtils.restart());
        popupMenu.add(item);
        // 退出
        popupMenu.addSeparator();
        item = new MenuItem(TitleConsts.EXIT);
        item.addActionListener(e -> WeUtils.exitSystem());
        popupMenu.add(item);
        // 添加系统托盘图标
        try {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(getClass().getResourceAsStream(ViewConsts.ICON));
            TrayIcon trayIcon = new TrayIcon(image, FinalUtils.getAppTitle(), popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(FinalUtils.getAppTitle());
            trayIcon.addMouseListener(new TrayMouseListener());
            tray.add(trayIcon);
            isTraySuccess = true;
        } catch (Exception e) {
            FxDialogs.showException(TipConsts.TRAY_ERROR, e);
        }
    }

    private void addQuickOpenMenu(Menu menu) {
        menu.add(FxUtils.createTrayMenuItem("配置文件", e -> FinalUtils.openConfig()));
        menu.add(FxUtils.createTrayMenuItem("日志文件", e -> FxUtils.openFile(FileConsts.LOG)));
        menu.addSeparator();
        menu.add(FxUtils.createTrayMenuItem("工作目录", e -> FxUtils.openFile(FileUtils.currentWorkDir())));
        menu.add(FxUtils.createTrayMenuItem("插件目录", e -> FinalUtils.openPluginFolder()));
        menu.add(FxUtils.createTrayMenuItem("日志目录", e -> FxUtils.openFile(FileConsts.LOG_FOLDER)));
        menu.addSeparator();
        menu.add(FxUtils.createTrayMenuItem("插件仓库", e -> FxUtils.openLink(TipConsts.REPO_LINK)));
    }

    private static class TrayMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == IntegerConsts.TWO) {
                // 双击图标
                Platform.runLater(FxUtils::toggleStage);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            log.debug("mouse pressed: {}", e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {log.debug("mouse released: {}", e.getPoint());}

        @Override
        public void mouseEntered(MouseEvent e) {log.debug("mouse entered: {}", e.getPoint());}

        @Override
        public void mouseExited(MouseEvent e) {log.debug("mouse exited: {}", e.getPoint());}
    }
}
