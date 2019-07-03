package org.code4everything.wetool.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.code4everything.wetool.util.WeUtils;
import org.code4everything.wetool.factor.BeanFactory;
import org.code4everything.wetool.model.ConfigModel;

/**
 * @author pantao
 * @since 2018/4/4
 */
public class CharsetConverterController {

    private static final String BASE64 = "BASE64";

    @FXML
    public TextArea originalContent;

    @FXML
    public ComboBox<String> originalCharset;

    @FXML
    public ComboBox<String> convertCharset;

    @FXML
    public TextArea convertedContent;

    @FXML
    private void initialize() {
        // 支持的编码
        String[] charset = {"UTF-8", "ISO-8859-1", "GBK", BASE64};

        // 添加至下拉框
        originalCharset.getItems().addAll(charset);
        originalCharset.getSelectionModel().selectFirst();
        convertCharset.getItems().addAll(charset);
        convertCharset.getSelectionModel().selectFirst();

        // 监听下拉框事件
        originalCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        convertCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());

        // 设置文本框
        originalContent.textProperty().addListener((o, ov, nv) -> convert());
        originalContent.setWrapText(ConfigModel.isAutoWrap());
        convertedContent.setWrapText(ConfigModel.isAutoWrap());

        // 注册
        BeanFactory.register(this);
    }

    private void convert() {
        String originalText = originalContent.getText();
        if (StrUtil.isNotEmpty(originalText)) {
            String srcCharset = originalCharset.getSelectionModel().getSelectedItem();
            String destCharset = convertCharset.getSelectionModel().getSelectedItem();

            String result;
            boolean baseDecode = BASE64.equals(srcCharset);
            boolean baseEncode = BASE64.equals(destCharset);
            if (baseDecode && baseEncode) {
                result = originalText;
            } else if (baseDecode) {
                result = Base64.decodeStr(originalText);
            } else if (baseEncode) {
                result = Base64.encode(originalText);
            } else {
                // 非BASE64编码
                result = CharsetUtil.convert(originalText, srcCharset, destCharset);
            }
            convertedContent.setText(result);
        }
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(originalContent, event);
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }
}
