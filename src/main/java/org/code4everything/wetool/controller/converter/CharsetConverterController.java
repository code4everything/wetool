package org.code4everything.wetool.controller.converter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.thirdparty.EncodingDetect;
import org.code4everything.wetool.util.FinalUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author pantao
 * @since 2018/4/4
 */
@Slf4j
public class CharsetConverterController extends AbstractConverter {

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
    public TextField fileCharset;

    @FXML
    public TextField filePath;

    @FXML
    public ComboBox<String> targetCharset;

    @FXML
    public CheckBox chooseFolderCB;

    @FXML
    private void initialize() {
        log.info("open tab for charset converter");
        FinalUtils.registerView(TitleConsts.CHARSET_CONVERTER, this);
        // 支持的编码
        String[] charset = {CharsetUtil.UTF_8, CharsetUtil.ISO_8859_1, CharsetUtil.GBK, BASE64};

        // 添加至下拉框
        originalCharset.getItems().addAll(charset);
        originalCharset.getSelectionModel().selectFirst();
        convertCharset.getItems().addAll(charset);
        convertCharset.getSelectionModel().selectFirst();
        targetCharset.getItems().addAll(CharsetUtil.UTF_8, CharsetUtil.GBK, CharsetUtil.ISO_8859_1);
        targetCharset.getSelectionModel().selectFirst();

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
            charset = CharsetUtil.UTF_8;
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

    public void convert2File() {
        if (StrUtil.isEmpty(filePath.getText()) || fileCharset.getText().equals(targetCharset.getValue())) {
            return;
        }
        File file = new File(filePath.getText());
        if (file.exists()) {
            String content = FileUtil.readString(file, fileCharset.getText());
            Charset charset = Charset.forName(targetCharset.getValue());
            FileUtil.writeString(content, file, charset);
            fileCharset.setText(charset.toString());
        }
    }

    public void batchConvert() {
        if (StrUtil.isEmpty(targetCharset.getValue())) {
            FxDialogs.showInformation(AppConsts.Title.APP_TITLE, "请选择目标编码！");
            return;
        }
        final Charset charset = Charset.forName(targetCharset.getValue());
        if (chooseFolderCB.isSelected()) {
            FxUtils.chooseFolder(file -> {
                batchConvert(Lists.newArrayList(file), charset);
                FxDialogs.showSuccess();
            });
        } else {
            FxUtils.chooseFiles(files -> {
                batchConvert(files, charset);
                FxDialogs.showSuccess();
            });
        }
    }

    private void batchConvert(List<File> files, final Charset charset) {
        if (CollUtil.isEmpty(files)) {
            return;
        }
        files.forEach(file -> {
            if (file.exists() && !file.isHidden()) {
                if (file.isFile()) {
                    String srcCharset = EncodingDetect.getJavaEncode(file.getAbsolutePath());
                    FileUtil.writeString(FileUtil.readString(file, srcCharset), file, charset);
                }
                if (file.isDirectory()) {
                    File[] fileArr = file.listFiles();
                    if (ArrayUtil.isNotEmpty(fileArr)) {
                        batchConvert(Arrays.asList(fileArr), charset);
                    }
                }
            }
        });
    }
}
