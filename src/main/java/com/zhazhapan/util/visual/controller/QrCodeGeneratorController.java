package com.zhazhapan.util.visual.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;

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
        ControllerModel.setQrCodeGeneratorController(this);
    }

    public void generateQrCode() {
        File image = new File(ValueConsts.USER_DESKTOP + ValueConsts.SEPARATOR + "qrcode.jpg");
        try {
            QrCodeUtil.generate(content.getText(), (int) qrCode.getFitWidth(), (int) qrCode.getFitHeight(), image);
            qrCode.setImage(new Image(new FileInputStream(image)));
            image.delete();
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.QR_CODE_ERROR);
        }
    }
}
