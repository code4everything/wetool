package com.zhazhapan.util.visual.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.ThreadPool;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class JsonParserController {

    @FXML
    public TextArea jsonContent;

    @FXML
    public TextArea parsedJsonContent;

    @FXML
    public TextField jsonPath;

    @FXML
    private void initialize() {
        jsonContent.setWrapText(ConfigModel.isAutoWrap());
        parsedJsonContent.setWrapText(ConfigModel.isAutoWrap());
        ControllerModel.setJsonParserController(this);
    }

    public void parseJson() {
        String json = jsonContent.getText();
        String path = jsonPath.getText();
        ThreadPool.executor.submit(() -> {
            try {
                JSONArray jsonArray = JSON.parseArray("[" + json + "]");
                Object object = JSONPath.eval(jsonArray, "[0]");
                String parsedJson;
                if (Checker.isEmpty(path)) {
                    parsedJson = object.toString();
                } else {
                    parsedJson = JSONPath.eval(object, (object instanceof JSONArray ? "" : ".") + path).toString();
                }
                Platform.runLater(() -> parsedJsonContent.setText(Formatter.formatJson(Checker.checkNull(parsedJson))));
            } catch (Exception e) {
                Platform.runLater(() -> Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts
                        .PARSE_JSON_ERROR));
            }
        });
    }

    public void seeJsonPathGrammar() {
        WeUtils.openLink(LocalValueConsts.JSON_PATH_GRAMMAR_URL);
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(jsonContent, event);
    }

    public void jsonPathEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            parseJson();
        }
    }
}
