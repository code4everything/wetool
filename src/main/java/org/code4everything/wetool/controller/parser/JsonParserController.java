package org.code4everything.wetool.controller.parser;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;

/**
 * @author pantao
 * @since 2018/3/31
 */
@Slf4j
public class JsonParserController implements BaseViewController {

    private final WeConfig config = WeUtils.getConfig();

    @FXML
    public TextArea jsonContent;

    @FXML
    public TextArea parsedJsonContent;

    @FXML
    public TextField jsonPath;

    @FXML
    private void initialize() {
        log.info("open tab for json parser");
        FinalUtils.registerView(TitleConsts.JSON_PARSER, this);
        jsonContent.setWrapText(config.getAutoWrap());
        parsedJsonContent.setWrapText(config.getAutoWrap());
    }

    public void parseJson() {
        String json = jsonContent.getText();
        String path = jsonPath.getText();
        if (StrUtil.isEmpty(path)) {
            parsedJsonContent.setText(json);
            return;
        }
        ThreadUtil.execute(() -> {
            try {
                log.info("parse json use path: {}", path);
                String parsedJson = JSON.toJSONString(JSONPath.extract(json, path), true);
                Platform.runLater(() -> parsedJsonContent.setText(parsedJson));
            } catch (Exception e) {
                FxDialogs.showException(TipConsts.JSON_PARSE_ERROR, e);
            }
        });
    }

    public void seeJsonPathGrammar() {
        FxUtils.openLink("https://github.com/alibaba/fastjson/wiki/JSONPath");
    }

    public void keyReleased(KeyEvent keyEvent) {
        FxUtils.enterDo(keyEvent, this::parseJson);
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.dropFileContent(jsonContent, event);
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
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
