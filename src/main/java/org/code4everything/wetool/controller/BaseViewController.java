package org.code4everything.wetool.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2019/7/4
 **/
public interface BaseViewController {

    default void openMultiFiles(List<File> files) {}

    default void openFile(File file) {
        openFile(FileUtil.readUtf8String(file));
    }

    default void openFile(String content) {}

    default void saveFile(File file) {
        String content = saveContent();
        if (StrUtil.isNotEmpty(content)) {
            FileUtil.writeUtf8String(content, file);
        }
    }

    default String saveContent() {return "";}
}
