package com.zhazhapan.util.visual;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.Utils;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.controller.FileManagerController;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class WeUtils {

    public static String replaceVariable(String s) {
        return Checker.checkNull(s).replaceAll(LocalValueConsts.DATE_VARIABLE, Formatter.dateToString(new Date()))
                .replaceAll(LocalValueConsts.TIME_VARIABLE, Formatter.datetimeToCustomString(new Date(),
                        LocalValueConsts.TIME_FORMAT));
    }

    public static int stringToInt(String integer) {
        int result = Formatter.stringToInt(integer);
        return result > -1 ? result : 0;
    }

    @SuppressWarnings("unchecked")
    public static void putFilesInSelectedListViewOfRenameTab(Object files) {
        FileManagerController fileManagerController = ControllerModel.getFileManagerController();
        if (Checker.isNotNull(fileManagerController)) {
            ObservableList items = fileManagerController.selectedFilesOfRenameTab.getItems();
            if (files instanceof List) {
                putFilesInListView((List<File>) files, items);
            } else if (files instanceof File) {
                putFilesInListView((File) files, items);
            }
        }
    }

    public static void putFilesInListView(List<File> files, ObservableList items) {
        if (Checker.isNotEmpty(files)) {
            files.forEach(file -> putFilesInListView(file, items));
        }
    }

    @SuppressWarnings("unchecked")
    private static void putFilesInListView(File file, ObservableList items) {
        if (file.isDirectory()) {
            putFilesInListView((List<File>) FileExecutor.listFiles(file, null, ValueConsts.TRUE), items);
        } else if (!items.contains(file)) {
            items.add(file);
        }
    }

    public static void saveFile(File file, String fileContent) {
        try {
            FileExecutor.saveFile(file, fileContent);
        } catch (IOException e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.SAVE_FILE_ERROR);
        }
    }

    public static String readFile(File file) {
        String result;
        try {
            result = FileExecutor.readFile(file);
        } catch (IOException e) {
            result = "";
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.READ_FILE_ERROR);
        }
        return result;
    }

    public static File getSaveFile() {
        return getFileChooser().showSaveDialog(WeToolApplication.stage);
    }

    public static List<File> getChooseFiles() {
        return getFileChooser().showOpenMultipleDialog(WeToolApplication.stage);
    }

    public static File getChooseFile() {
        return getFileChooser().showOpenDialog(WeToolApplication.stage);
    }

    private static FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(LocalValueConsts.MAIN_TITLE);
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return chooser;
    }

    public static void openLink(String url) {
        try {
            Utils.openLink(url);
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.OPEN_LINK_ERROR);
        }
    }

    public static void exitSystem() {
        System.exit(ValueConsts.ZERO_INT);
    }

    public static VBox loadFxml(String url) {
        try {
            return FXMLLoader.load(WeUtils.class.getResource(url));
        } catch (Exception e) {
            Alerts.showException(ValueConsts.FATAL_ERROR, e);
            return null;
        }
    }
}
