package com.zhazhapan.util.visual;

import com.zhazhapan.config.JsonParser;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * @author pantao
 * @since 2018/3/30
 */
public class WeToolApplication extends Application {

    private static final String WIDTH_PATH = "initialize.width";

    private static final String HEIGHT_PATH = "initialize.height";

    public static Stage stage = null;

    public static JsonParser config = new JsonParser();

    public static void main(String[] args) {
        try {
            config.setJsonObject(FileExecutor.read(WeToolApplication.class.getResourceAsStream(LocalValueConsts
                    .CONFIG_PATH)));
        } catch (IOException e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.LOAD_CONFIG_ERROR);
        }
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

        if (config.hasJsonObject()) {
            try {
                stage.setWidth(config.getDoubleUseEval(WIDTH_PATH));
                stage.setHeight(config.getDoubleUseEval(HEIGHT_PATH));
            } catch (Exception e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.LOAD_CONFIG_ERROR);
            }
        }
        stage.show();
        WeToolApplication.stage = stage;
    }
}
