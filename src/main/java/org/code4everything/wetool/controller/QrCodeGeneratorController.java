package org.code4everything.wetool.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.zhazhapan.util.dialog.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.WeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author pantao
 * @since 2018/4/4
 */
public class QrCodeGeneratorController {

    private final WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    public TextArea content;

    @FXML
    public ImageView qrCode;

    @FXML
    private void initialize() {
        BeanFactory.register(this);
        content.setWrapText(config.getAutoWrap());
    }

    public void generateQrCode() {
        int size = (int) Double.min(qrCode.getFitHeight(), qrCode.getFitWidth());
        File image = new File(FileUtils.currentWorkDir("qrcode.jpg"));
        try {
            QrCodeUtil.generate(content.getText(), size, size, image);
            InputStream is = new FileInputStream(image);
            qrCode.setImage(new Image(is));
            is.close();
            image.delete();
        } catch (Exception e) {
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.QR_CODE_ERROR);
        }
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(content, event);
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }
}
