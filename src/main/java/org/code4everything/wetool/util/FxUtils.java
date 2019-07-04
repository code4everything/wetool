package org.code4everything.wetool.util;

import cn.hutool.core.io.FileUtil;
import com.zhazhapan.util.Checker;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2019/7/4
 **/
@UtilityClass
public class FxUtils {

    public static void putDraggedFileContent(TextInputControl control, DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            control.setText(FileUtil.readUtf8String(files.get(0)));
        }
    }

    public static void acceptCopyMode(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }
}
