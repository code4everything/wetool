package org.code4everything.wetool.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author pantao
 * @since 2019/7/5
 **/
@UtilityClass
public class FxDialogs {

    public static void showSuccess() {
        showInformation(null, TipConsts.OPERATION_SUCCESS);
    }

    public static void showInformation(String header, String content) {
        showAlert(header, content, Alert.AlertType.INFORMATION);
    }

    public static void showError(String content) {
        showAlert(null, content, Alert.AlertType.ERROR);
    }

    public static void showException(String header, Exception e) {
        Platform.runLater(() -> {
            Alert alert = makeAlert(header, "错误信息追踪：", Alert.AlertType.ERROR, Modality.APPLICATION_MODAL);

            // 输出异常信息
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String exception = stringWriter.toString();

            // 异常信息容易
            TextArea textArea = new TextArea(exception);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            // 设置大小
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            // 添加至面板
            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            gridPane.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(gridPane);
            alert.showAndWait();
        });
    }

    private static void showAlert(String header, String content, Alert.AlertType alertType) {
        Platform.runLater(() -> makeAlert(header, content, alertType, Modality.NONE).showAndWait());
    }

    private static Alert makeAlert(String header, String content, Alert.AlertType alertType, Modality modality) {
        Alert alert = new Alert(alertType);

        alert.setTitle(TitleConsts.APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.initModality(modality);
        alert.initStyle(StageStyle.DECORATED);

        return alert;
    }
}
