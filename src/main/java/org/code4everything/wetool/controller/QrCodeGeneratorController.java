package org.code4everything.wetool.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.dialog.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.code4everything.wetool.WeUtils;
import org.code4everything.wetool.constant.LocalValueConsts;
import org.code4everything.wetool.factor.BeanFactory;
import org.code4everything.wetool.model.ConfigModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author pantao
 * @since 2018/4/4
 */
public class QrCodeGeneratorController {

    @FXML
    public TextArea content;

    @FXML
    public ImageView qrCode;

    @FXML
    private void initialize() {
        content.setWrapText(ConfigModel.isAutoWrap());
        BeanFactory.register(this);
    }

    public void generateQrCode() {
        int size = (int) Double.min(qrCode.getFitHeight(), qrCode.getFitWidth());
        File image = new File(ValueConsts.USER_DESKTOP + ValueConsts.SEPARATOR + "qrcode.jpg");
        try {
            QrCodeUtil.generate(content.getText(), size, size, image);
            InputStream is = new FileInputStream(image);
            qrCode.setImage(new Image(is));
            is.close();
            image.delete();
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.QR_CODE_ERROR);
        }
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putDragFileInTextArea(content, event);
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }
}
