package org.code4everything.wetool.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.zhazhapan.util.dialog.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author pantao
 * @since 2018/4/4
 */
public class QrCodeGeneratorController implements BaseViewController {

    private static final File TEMP_FILE = new File(FileUtils.currentWorkDir("qrcode.jpg"));

    private final WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    public TextArea content;

    @FXML
    public ImageView qrCode;

    private InputStream is;

    @FXML
    private void initialize() {
        BeanFactory.registerView(TitleConsts.QR_CODE_GENERATOR, this);
        content.setWrapText(config.getAutoWrap());
    }

    public void generateQrCode() {
        int size = (int) Math.min(qrCode.getFitHeight(), qrCode.getFitWidth());
        try {
            QrCodeUtil.generate(content.getText(), size, size, TEMP_FILE);
            is = new FileInputStream(TEMP_FILE);
            qrCode.setImage(new Image(is));
            is.close();
            FileUtil.del(TEMP_FILE);
        } catch (Exception e) {
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.QR_CODE_ERROR);
        }
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.putDraggedFileContent(content, event);
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
    }

    @Override
    public void saveFile(File file) {
        if (Objects.isNull(is)) {
            return;
        }
        FileUtil.writeFromStream(is, file);
    }

    @Override
    public void setFileContent(String content) {
        this.content.setText(content);
    }
}
