package com.zhazhapan.util.visual.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

/**
 * @author pantao
 * @since 2018/4/4
 */
public class CharsetConverterController {

    private final String BASE64 = "BASE64";

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
        String[] charset = {"UTF-8", "ISO-8859-1", "GBK", BASE64};
        originalCharset.getItems().addAll(charset);
        originalCharset.getSelectionModel().selectFirst();
        convertCharset.getItems().addAll(charset);
        convertCharset.getSelectionModel().selectFirst();
        originalCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        convertCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        originalContent.textProperty().addListener((o, ov, nv) -> convert());
        originalContent.setWrapText(ConfigModel.isAutoWrap());
        convertedContent.setWrapText(ConfigModel.isAutoWrap());
        ControllerModel.setCharsetConverterController(this);
    }

    private void convert() {
        String originalText = originalContent.getText();
        if (Checker.isNotEmpty(originalText)) {
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
