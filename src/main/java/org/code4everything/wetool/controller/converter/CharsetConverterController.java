package org.code4everything.wetool.controller.converter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.thirdparty.EncodingDetect;

import java.io.File;

/**
 * @author pantao
 * @since 2018/4/4
 */
@Slf4j
public class CharsetConverterController extends AbstractConverter {

    private final String BASE64 = "BASE64";

    private final String UTF8 = "UTF-8";

    @FXML
    public TextArea originalContent;

    @FXML
    public ComboBox<String> originalCharset;

    @FXML
    public ComboBox<String> convertCharset;

    @FXML
    public TextArea convertedContent;

    @FXML
    public TextField fileCharset;

    @FXML
    public TextField filePath;

    @FXML
    private void initialize() {
        log.info("open tab for charset converter");
        BeanFactory.registerView(TitleConsts.CHARSET_CONVERTER, this);
        // 支持的编码
        String[] charset = {UTF8, "ISO-8859-1", "GBK", BASE64};

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
        String srcCharset = originalCharset.getValue();
        String destCharset = convertCharset.getValue();

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
    public void openFile(File file) {
        String charset = originalCharset.getValue();
        if (BASE64.equals(charset)) {
            charset = UTF8;
        }
        originalContent.setText(FileUtil.readString(file, charset));
    }

    public void recognizeCharset() {
        FxUtils.chooseFile(file -> {
            // 设置文件路径
            filePath.setText(file.getAbsolutePath());
            fileCharset.setText(EncodingDetect.getJavaEncode(file.getAbsolutePath()));
        });
    }

    public void readByCharset() {
        originalCharset.setValue(fileCharset.getText());
        openFile(new File(filePath.getText()));
    }

    public void openFileBySystem() {
        FxUtils.openFile(filePath.getText());
    }
}
