package org.code4everything.wetool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.ObjectUtils;
import org.code4everything.boot.base.constant.IntegerConsts;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.controller.MainController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.config.WeStart;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Objects;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class WeApplication extends Application {

    protected static MainController mainController;

    private static Menu pluginMenu;

    private Stage stage;

    private boolean isTraySuccess = false;

    public static void main(String[] args) {
        log.info("starting wetool on os: {}", SystemUtil.getOsInfo().getName());
        parseConfig();
        launch(args);
    }

    public static void addIntoPluginMenu(MenuItem menuItem) {
        if (ObjectUtils.isNotNull(pluginMenu, menuItem)) {
            Platform.runLater(() -> pluginMenu.add(menuItem));
        }
    }

    public static void setMainController(MainController mainController) {
        WeApplication.mainController = mainController;
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
        // 加载主界面
        Pane root = FxUtils.loadFxml(ViewConsts.MAIN);
        if (Objects.isNull(root)) {
            FxDialogs.showError(TipConsts.INIT_ERROR);
            WeUtils.exitSystem();
        }
        // 设置标题
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ViewConsts.ICON)));
        stage.setTitle(AppConsts.Title.APP_TITLE);
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

        if (WeUtils.getConfig().getInitialize().getHide()) {
            hideStage();
        } else {
            stage.show();
        }
        log.info("wetool started");
    }

    private void hideStage() {
        if (isTraySuccess) {
            stage.hide();
        } else {
            stage.setIconified(true);
        }
    }

    private void setQuickStartMenu(Menu menu, List<WeStart> starts) {
        starts.forEach(start -> {
            if (CollUtil.isEmpty(start.getSubStarts())) {
                // 添加子菜单
                MenuItem item = new MenuItem(start.getAlias());
                item.addActionListener(e -> FxUtils.openFile(start.getLocation()));
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
        List<WeStart> starts = WeUtils.getConfig().getQuickStarts();
        if (CollUtil.isNotEmpty(starts)) {
            Menu menu = new Menu(TitleConsts.QUICK_START);
            setQuickStartMenu(menu, starts);
            popupMenu.add(menu);
            popupMenu.addSeparator();
        }
        // 插件菜单
        pluginMenu = new Menu(TitleConsts.PLUGIN);
        popupMenu.add(pluginMenu);
        popupMenu.addSeparator();
        // 显示
        MenuItem item = new MenuItem(TitleConsts.SHOW);
        item.addActionListener(e -> Platform.runLater(() -> stage.show()));
        popupMenu.add(item);
        // 隐藏
        item = new MenuItem(TitleConsts.HIDE);
        item.addActionListener(e -> Platform.runLater(() -> stage.hide()));
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
            TrayIcon trayIcon = new TrayIcon(image, AppConsts.Title.APP_TITLE, popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(AppConsts.Title.APP_TITLE);
            trayIcon.addMouseListener(new TrayMouseListener());
            tray.add(trayIcon);
            isTraySuccess = true;
        } catch (Exception e) {
            FxDialogs.showException(TipConsts.TRAY_ERROR, e);
        }
    }

    private class TrayMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == IntegerConsts.TWO) {
                // 双击图标
                Platform.runLater(() -> {
                    if (stage.isShowing()) {
                        stage.hide();
                    } else {
                        stage.show();
                    }
                });
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }
}
