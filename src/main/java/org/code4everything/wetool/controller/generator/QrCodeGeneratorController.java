package org.code4everything.wetool.controller.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @author pantao
 * @since 2018/4/4
 */
@Slf4j
public class QrCodeGeneratorController implements BaseViewController {

    private final WeConfig config = WeUtils.getConfig();

    @FXML
    public TextArea content;

    @FXML
    public ImageView qrCode;

    private byte[] bytes;

    @FXML
    private void initialize() {
        log.info("open tab for qr code generator");
        BeanFactory.registerView(TitleConsts.QR_CODE_GENERATOR, this);
        content.setWrapText(config.getAutoWrap());
    }

    @SneakyThrows
    public void generateQrCode() {
        // 计算二维码大小
        int size = (int) Math.min(qrCode.getFitHeight(), qrCode.getFitWidth());
        // 压缩并记录进日志
        String compress = WeUtils.compressString(content.getText());
        log.info("generate qr code for content: {}", compress);
        // 生成二维码
        bytes = QrCodeUtil.generatePng(content.getText(), size, size);
        @Cleanup InputStream inputStream = new ByteArrayInputStream(bytes);
        qrCode.setImage(new Image(inputStream));
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.dropFileContent(content, event);
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
    }

    @Override
    public void saveFile(File file) {
        if (ArrayUtil.isEmpty(bytes)) {
            return;
        }
        FileUtil.writeBytes(bytes, file);
    }

    @Override
    public void setFileContent(String content) {
        this.content.setText(content);
    }
}
