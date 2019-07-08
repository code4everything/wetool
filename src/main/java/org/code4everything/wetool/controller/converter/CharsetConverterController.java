package org.code4everything.wetool.controller.converter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;
import org.code4everything.wetool.util.WeUtils;

/**
 * @author pantao
 * @since 2018/4/4
 */
@Slf4j
public class CharsetConverterController extends AbstractConverter {

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
        log.info("load tab charset converter");
        BeanFactory.registerView(TitleConsts.CHARSET_CONVERTER, this);
        // 支持的编码
        String[] charset = {"UTF-8", "ISO-8859-1", "GBK", BASE64};

        // 添加至下拉框
        originalCharset.getItems().addAll(charset);
        originalCharset.getSelectionModel().selectFirst();
        convertCharset.getItems().addAll(charset);
        convertCharset.getSelectionModel().selectFirst();

        super.initConverter(originalContent, convertedContent, originalCharset, convertCharset);
    }

    @Override
    void convert() {
        String originalText = originalContent.getText();
        if (StrUtil.isEmpty(originalText)) {
            return;
        }
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
        String compress = WeUtils.compressString(originalText);
        log.info("convert charset {} to {} for content: {}", srcCharset, destCharset, compress);
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.dropFileContent(originalContent, event);
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
    public void setFileContent(String content) {
        originalContent.setText(content);
    }
}
