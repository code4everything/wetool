package org.code4everything.wetool.controller.converter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;

import java.io.File;

/**
 * @author pantao
 * @since 2019/7/8
 **/
@Slf4j
public class NaryConverterController extends AbstractConverter {

    private static final int INT_BIN_LEN = 32;

    private static final int INT_HEX_LEN = 8;

    @FXML
    public TextArea originalContent;

    @FXML
    public ComboBox<String> originalNary;

    @FXML
    public ComboBox<String> convertNary;

    @FXML
    public TextArea convertedContent;

    @FXML
    private void initialize() {
        log.info("open tab for nary converter");
        BeanFactory.registerView(TitleConsts.NARY_CONVERTER, this);
        // 支持的进制
        String[] nary = {"二进制", "十六进制"};

        // 添加至下拉框
        originalNary.getItems().addAll(nary);
        originalNary.getSelectionModel().selectFirst();
        convertNary.getItems().addAll(nary);
        convertNary.getSelectionModel().selectLast();

        super.initConverter(originalContent, convertedContent, originalNary, convertNary);
    }

    @Override
    void convert() {
        String originalText = originalContent.getText();
        if (StrUtil.isEmpty(originalText)) {
            return;
        }
        int srcIdx = originalNary.getSelectionModel().getSelectedIndex();
        int destIdx = convertNary.getSelectionModel().getSelectedIndex();

        if (srcIdx == destIdx) {
            // 没有进制的转换
            convertedContent.setText(originalText);
            return;
        }
        if (destIdx == 1) {
            convertedContent.setText(convert2Hex(originalText));
        } else {
            convertedContent.setText(convert2Binary(originalText));
        }
    }

    private String convert2Binary(String hex) {
        StringBuilder builder = new StringBuilder();
        for (int len = hex.length(); len > 0; len -= INT_HEX_LEN) {
            int dec = Integer.parseUnsignedInt(hex.substring(Math.max(0, len - INT_HEX_LEN), len), 16);
            builder.insert(0, Integer.toBinaryString(dec));
        }
        return builder.toString();
    }

    private String convert2Hex(String binary) {
        StringBuilder builder = new StringBuilder();
        for (int len = binary.length(); len > 0; len -= INT_BIN_LEN) {
            int dec = Integer.parseUnsignedInt(binary.substring(Math.max(0, len - INT_BIN_LEN), len), 2);
            builder.insert(0, Integer.toHexString(dec));
        }
        return builder.toString();
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.dropFiles(event, files -> openFile(files.get(0)));
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
    }

    @Override
    public String getSavingContent() {
        return convertedContent.getText();
    }

    @Override
    public void openFile(File file) {
        byte[] bytes = FileUtil.readBytes(file);
        if (ArrayUtil.isEmpty(bytes)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (originalNary.getSelectionModel().getSelectedIndex() == 0) {
            for (byte b : bytes) {
                builder.append(Integer.toBinaryString((b & 0xff) + 0x100).substring(1));
            }
        } else {
            for (byte b : bytes) {
                builder.append(Integer.toHexString((b & 0xff) + 0x100).substring(1));
            }
        }
        originalContent.setText(builder.toString());
    }
}
