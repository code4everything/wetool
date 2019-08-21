package org.code4everything.wetool.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.function.VoidFunction;
import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author pantao
 * @since 2019/7/4
 **/
@Slf4j
@UtilityClass
public class FxUtils {

    public static void saveFile(Callable<File> callable) {
        File file = getFileChooser().showSaveDialog(BeanFactory.get(Stage.class));
        handleFileCallable(file, callable);
    }

    public static void chooseFiles(Callable<List<File>> callable) {
        List<File> files = getFileChooser().showOpenMultipleDialog(BeanFactory.get(Stage.class));
        handleFileListCallable(files, callable);
    }

    public static void chooseFile(Callable<File> callable) {
        File file = getFileChooser().showOpenDialog(BeanFactory.get(Stage.class));
        handleFileCallable(file, callable);
    }

    public static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException | IOException e) {
            FxDialogs.showException(TipConsts.OPEN_LINK_ERROR, e);
        }
    }

    public static void openFile(String file) {
        try {
            Desktop.getDesktop().open(FileUtil.file(file));
        } catch (Exception e) {
            FxDialogs.showException(TipConsts.OPEN_FILE_ERROR, e);
        }
    }

    public static void restart() {
        // 获取当前程序运行路径
        final String jarPath = System.getProperty("java.class.path");
        // 文件名的截取索引
        final int idx = Math.max(jarPath.lastIndexOf('/'), jarPath.lastIndexOf('\\')) + 1;
        ThreadUtil.execute(() -> RuntimeUtil.execForStr("java -jar ./" + jarPath.substring(idx)));
        WeUtils.exitSystem();
    }

    public static void dropFileContent(TextInputControl control, DragEvent event) {
        dropFiles(event, files -> control.setText(FileUtil.readUtf8String(files.get(0))));
    }

    public static void dropFiles(DragEvent event, Map<Object, Callable<List<File>>> eventCallableMap) {
        handleFileListCallable(event.getDragboard().getFiles(), eventCallableMap.get(event.getSource()));
    }

    public static void dropFiles(DragEvent event, Callable<List<File>> callable) {
        handleFileListCallable(event.getDragboard().getFiles(), callable);
    }

    public static void acceptCopyMode(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    public static void enterDo(KeyEvent event, VoidFunction function) {
        if (event.getCode() == KeyCode.ENTER) {
            function.call();
        }
    }

    public static VBox loadFxml(String url) {
        try {
            return FXMLLoader.load(WeUtils.class.getResource(url));
        } catch (Exception e) {
            FxDialogs.showException(TipConsts.FXML_ERROR, e);
            return null;
        }
    }

    private static FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(TitleConsts.APP_TITLE);
        chooser.setInitialDirectory(new File(BeanFactory.get(WeConfig.class).getFileChooserInitDir()));
        return chooser;
    }

    private static void handleFileListCallable(List<File> files, Callable<List<File>> callable) {
        if (CollUtil.isEmpty(files) || Objects.isNull(callable)) {
            return;
        }
        BeanFactory.get(WeConfig.class).setFileChooserInitDir(files.get(0).getParent());
        callable.call(files);
    }

    private static void handleFileCallable(File file, Callable<File> callable) {
        if (Objects.isNull(file) || Objects.isNull(callable)) {
            return;
        }
        BeanFactory.get(WeConfig.class).setFileChooserInitDir(file.getParent());
        callable.call(file);
    }
}
