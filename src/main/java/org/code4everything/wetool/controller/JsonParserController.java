package org.code4everything.wetool.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.ThreadPool;
import com.zhazhapan.util.dialog.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ValueConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.WeUtils;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class JsonParserController implements BaseViewController {

    private final WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    public TextArea jsonContent;

    @FXML
    public TextArea parsedJsonContent;

    @FXML
    public TextField jsonPath;

    @FXML
    private void initialize() {
        BeanFactory.registerView(TitleConsts.JSON_PARSER, this);
        jsonContent.setWrapText(config.getAutoWrap());
        parsedJsonContent.setWrapText(config.getAutoWrap());
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
                Platform.runLater(() -> Alerts.showError(TitleConsts.APP_TITLE, TipConsts.JSON_PARSE_ERROR));
            }
        });
    }

    public void seeJsonPathGrammar() {
        WeUtils.openLink(ValueConsts.JSON_HELPER_URL);
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(jsonContent, event);
    }

    public void jsonPathEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            parseJson();
        }
    }

    @Override
    public String getSavingContent() {
        return parsedJsonContent.getText();
    }

    @Override
    public void setFileContent(String content) {
        jsonContent.setText(content);
    }
}
