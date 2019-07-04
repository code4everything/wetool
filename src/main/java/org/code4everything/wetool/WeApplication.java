package org.code4everything.wetool;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhazhapan.util.dialog.Alerts;
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
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.controller.MainController;
import org.code4everything.wetool.factory.BeanFactory;
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
        log.info("run application......");
        // 解析配置文件
        String path = FileUtils.currentWorkDir("we-config.json");
        WeConfig config = JSONObject.parseObject(FileUtil.readUtf8String(path), WeConfig.class);
        BeanFactory.register(config);
        // 启动应用
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BeanFactory.register(stage);
        // 加载主界面
        VBox root = WeUtils.loadFxml(ViewConsts.MAIN);
        if (Objects.isNull(root)) {
            Alerts.showError(com.zhazhapan.modules.constant.ValueConsts.FATAL_ERROR, TipConsts.INIT_ERROR);
            WeUtils.exitSystem();
        }
        // 设置标题
        stage.setScene(new Scene(root));
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
            enableTray();
        }
        stage.show();
        BeanFactory.get(MainController.class).loadTabs();
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
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.TRAY_ERROR);
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
