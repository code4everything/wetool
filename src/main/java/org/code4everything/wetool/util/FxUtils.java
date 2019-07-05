package org.code4everything.wetool.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.dialog.Alerts;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.function.VoidFunction;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * @author pantao
 * @since 2019/7/4
 **/
@Slf4j
@UtilityClass
public class FxUtils {

    public static File fileSaving() {
        return getFileChooser().showSaveDialog(BeanFactory.get(Stage.class));
    }

    public static void chooseFiles(ChooserCallable<List<File>> callable) {
        List<File> files = getFileChooser().showOpenMultipleDialog(BeanFactory.get(Stage.class));
        if (CollUtil.isEmpty(files) || Objects.isNull(callable)) {
            return;
        }
        callable.call(files);
    }

    public static void chooseFile(ChooserCallable<File> callable) {
        File file = getFileChooser().showOpenDialog(BeanFactory.get(Stage.class));
        if (Objects.isNull(file) || Objects.isNull(callable)) {
            return;
        }
        callable.call(file);
    }

    public static void showSuccess() {
        Alerts.showInformation(TitleConsts.APP_TITLE, TipConsts.OPERATION_SUCCESS);
    }

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

    private static FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(TitleConsts.APP_TITLE);
        chooser.setInitialDirectory(new File(BeanFactory.get(WeConfig.class).getFileChooserInitDir()));
        return chooser;
    }
}
