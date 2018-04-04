package com.zhazhapan.util.visual.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.visual.WeUtils;
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

    private final String BASE = "BASE64";

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
        String[] charset = {"UTF-8", "ISO-8859-1", "GBK", BASE};
        originalCharset.getItems().addAll(charset);
        originalCharset.getSelectionModel().selectFirst();
        convertCharset.getItems().addAll(charset);
        convertCharset.getSelectionModel().selectFirst();
        originalCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        convertCharset.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        originalContent.textProperty().addListener((o, ov, nv) -> convert());
        ControllerModel.setCharsetConverterController(this);
    }

    private void convert() {
        String originalText = Checker.checkNull(originalContent.getText());
        String srcCharset = originalCharset.getSelectionModel().getSelectedItem();
        String destCharset = convertCharset.getSelectionModel().getSelectedItem();
        String result;
        boolean isBase1 = BASE.equals(srcCharset);
        boolean isBase2 = BASE.equals(destCharset);
        if (isBase1 && isBase2) {
            result = originalText;
        } else if (isBase1) {
            result = Base64.decodeStr(originalText);
        } else if (isBase2) {
            result = Base64.encode(originalText);
        } else {
            result = CharsetUtil.convert(originalText, srcCharset, destCharset);
        }
        convertedContent.setText(result);
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(originalContent, event);
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }
}
