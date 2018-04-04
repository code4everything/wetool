package com.zhazhapan.util.visual;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author pantao
 * @since 2018/3/30
 */
public class WeToolApplication extends Application {

    static Stage stage = null;

    private TrayIcon trayIcon;

    private boolean isTraySuccess = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = WeUtils.loadFxml(LocalValueConsts.MAIN_VIEW);
        if (Checker.isNull(root)) {
            Alerts.showError(ValueConsts.FATAL_ERROR, LocalValueConsts.INIT_ERROR);
            WeUtils.exitSystem();
        }
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(getClass().getResourceAsStream(LocalValueConsts.ICON)));
        stage.setTitle(LocalValueConsts.MAIN_TITLE);
        stage.setOnCloseRequest((WindowEvent event) -> {
            if (isTraySuccess) {
                stage.hide();
            } else {
                stage.setIconified(true);
            }
            event.consume();
        });
        ConfigParser.parserConfig();
        stage.setWidth(ConfigModel.getWidth());
        stage.setHeight(ConfigModel.getHeight());
        stage.setFullScreen(ConfigModel.isFullscreen());
        ControllerModel.getMainController().loadTabs();
        WeToolApplication.stage = stage;
        enableTray();
        stage.show();
    }

    /**
     * 系统托盘
     */
    private void enableTray() {
        Platform.setImplicitExit(false);
        PopupMenu popupMenu = new PopupMenu();
        java.awt.MenuItem openItem = new java.awt.MenuItem(LocalValueConsts.SHOW);
        java.awt.MenuItem hideItem = new java.awt.MenuItem(LocalValueConsts.HIDE);
        java.awt.MenuItem quitItem = new java.awt.MenuItem(LocalValueConsts.EXIT);
        ActionListener actionListener = e -> {
            MenuItem item = (MenuItem) e.getSource();
            switch (item.getLabel()) {
                case LocalValueConsts.EXIT:
                    SystemTray.getSystemTray().remove(trayIcon);
                    WeUtils.exitSystem();
                    break;
                case LocalValueConsts.SHOW:
                    Platform.runLater(stage::show);
                    break;
                case LocalValueConsts.HIDE:
                    Platform.runLater(stage::hide);
                    break;
                default:
                    break;
            }
        };
        //双击事件方法
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.setImplicitExit(false);
                if (e.getClickCount() == ValueConsts.TWO_INT) {
                    Platform.runLater(() -> {
                        if (stage.isShowing()) {
                            stage.hide();
                        } else {
                            stage.show();
                        }
                    });
                }
            }
        };
        openItem.addActionListener(actionListener);
        quitItem.addActionListener(actionListener);
        hideItem.addActionListener(actionListener);
        popupMenu.add(openItem);
        popupMenu.add(hideItem);
        popupMenu.add(quitItem);
        try {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(getClass().getResourceAsStream(LocalValueConsts.ICON));
            trayIcon = new TrayIcon(image, LocalValueConsts.MAIN_TITLE, popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(LocalValueConsts.MAIN_TITLE);
            trayIcon.addMouseListener(mouseListener);
            tray.add(trayIcon);
            isTraySuccess = true;
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.TRAY_ERROR);
        }
    }
}
