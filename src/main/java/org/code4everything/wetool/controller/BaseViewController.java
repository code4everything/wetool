package org.code4everything.wetool.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2019/7/4
 **/
public interface BaseViewController {

    default void openMultiFiles(List<File> files) {}

    default void openFile(File file) {
        setFileContent(FileUtil.readUtf8String(file));
    }

    default void setFileContent(String content) {}

    default void saveFile(File file) {
        String content = getSavingContent();
        if (StrUtil.isNotEmpty(content)) {
            FileUtil.writeUtf8String(content, file);
        }
    }

    default String getSavingContent() {return "";}

    default void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    default void dragFileDropped(DragEvent event) {}
}
