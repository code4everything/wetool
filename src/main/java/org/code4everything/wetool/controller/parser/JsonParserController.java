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
import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.controller.BaseViewController;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxDialogs;
import org.code4everything.wetool.util.FxUtils;

/**
 * @author pantao
 * @since 2018/3/31
 */
@Slf4j
public class JsonParserController implements BaseViewController {

    private static final String JSON_HELPER_URL = "https://github.com/alibaba/fastjson/wiki/JSONPath";

    private final WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    public TextArea jsonContent;

    @FXML
    public TextArea parsedJsonContent;

    @FXML
    public TextField jsonPath;

    @FXML
    private void initialize() {
        log.info("open tab for json parser");
        BeanFactory.registerView(TitleConsts.JSON_PARSER, this);
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
        FxUtils.openLink(JSON_HELPER_URL);
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
