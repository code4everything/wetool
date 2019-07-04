package org.code4everything.wetool.util;

import cn.hutool.core.io.FileUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.dialog.Alerts;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.function.VoidFunction;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author pantao
 * @since 2019/7/4
 **/
@Slf4j
@UtilityClass
public class FxUtils {

    public static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException | IOException e) {
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.OPEN_LINK_ERROR);
        }
    }

    public static void putDraggedFileContent(TextInputControl control, DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            control.setText(FileUtil.readUtf8String(files.get(0)));
        }
    }

    public static void acceptCopyMode(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    public static void enterDo(KeyEvent event, VoidFunction function) {
        if (event.getCode() == KeyCode.ENTER) {
            function.call();
        }
    }
}
