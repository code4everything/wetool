package org.code4everything.wetool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.boot.base.constant.IntegerConsts;
import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxDialogs;
import org.code4everything.wetool.util.FxUtils;
import org.code4everything.wetool.util.WeUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class WeApplication extends Application {

    private Stage stage;

    private boolean isTraySuccess = false;

    public static void main(String[] args) {
        log.info("start wetool");
        log.info("current os: {}", SystemUtil.getOsInfo().getName());
        // 解析配置文件
        log.info("load config");
        String path = FileUtils.currentWorkDir("we-config.json");
        if (!FileUtil.exist(path)) {
            log.error("config not found");
            WeUtils.exitSystem();
        }
        WeConfig config = JSON.parseObject(FileUtil.readUtf8String(path), WeConfig.class);
        BeanFactory.register(config);
        // 启动应用
        log.info("load app gui");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BeanFactory.register(stage);
        // 加载主界面
        VBox root = FxUtils.loadFxml(ViewConsts.MAIN);
        if (Objects.isNull(root)) {
            FxDialogs.showError(TipConsts.INIT_ERROR);
            WeUtils.exitSystem();
        }
        // 设置标题
        stage.setScene(new Scene(Objects.requireNonNull(root)));
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ViewConsts.ICON)));
        stage.setTitle(TitleConsts.APP_TITLE);
        // 监听关闭事件
        stage.setOnCloseRequest((WindowEvent event) -> {
            if (isTraySuccess) {
                stage.hide();
            } else {
                stage.setIconified(true);
            }
            event.consume();
        });
        // 设置大小
        WeConfig config = BeanFactory.get(WeConfig.class);
        stage.setWidth(config.getInitialize().getWidth());
        stage.setHeight(config.getInitialize().getHeight());
        stage.setFullScreen(config.getInitialize().getFullscreen());

        if (WeUtils.isWindows()) {
            // 开启系统托盘
            log.info("system tray enabled");
            enableTray();
        }
        stage.show();
    }

    /**
     * 系统托盘
     */
    private void enableTray() {
        Platform.setImplicitExit(false);
        // 添加托盘邮件菜单
        PopupMenu popupMenu = new PopupMenu();
        // 显示
        MenuItem item = new MenuItem(TitleConsts.SHOW);
        item.addActionListener(e -> Platform.runLater(() -> stage.show()));
        popupMenu.add(item);
        // 隐藏
        item = new MenuItem(TitleConsts.HIDE);
        item.addActionListener(e -> Platform.runLater(() -> stage.hide()));
        popupMenu.add(item);
        // 重启
        item = new MenuItem(TitleConsts.RESTART);
        item.addActionListener(e -> FxUtils.restart());
        popupMenu.add(item);
        // 退出
        item = new MenuItem(TitleConsts.EXIT);
        item.addActionListener(e -> WeUtils.exitSystem());
        popupMenu.add(item);
        // 添加系统托盘图标
        try {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(getClass().getResourceAsStream(ViewConsts.ICON));
            TrayIcon trayIcon = new TrayIcon(image, TitleConsts.APP_TITLE, popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(TitleConsts.APP_TITLE);
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
