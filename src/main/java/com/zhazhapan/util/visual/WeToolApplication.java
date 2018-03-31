package com.zhazhapan.util.visual;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import javafx.application.Application;
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

    public static Stage stage = null;

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
            stage.setIconified(true);
            event.consume();
        });
        stage.show();
        WeToolApplication.stage = stage;
    }
}
