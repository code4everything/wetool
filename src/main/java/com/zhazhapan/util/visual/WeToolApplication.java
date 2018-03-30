package com.zhazhapan.util.visual;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author pantao
 * @since 2018/3/30
 */
public class WeToolApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            VBox root = FXMLLoader.load(getClass().getResource("/view/WeToolMainView.fxml"));
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/wetool.png")));
        } catch (Exception e) {
            Alerts.showFatalError(ValueConsts.FATAL_ERROR, "初始化失败，无法继续运行", e);
        }
        stage.setTitle(LocalValueConsts.MAIN_TITLE);
        stage.setOnCloseRequest((WindowEvent event) -> {
            stage.setIconified(true);
            event.consume();
        });
        stage.show();
    }
}
