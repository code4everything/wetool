package com.zhazhapan.util.visual;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.*;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.controller.FileManagerController;
import com.zhazhapan.util.visual.controller.WaveController;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.zhazhapan.util.visual.WeToolApplication.stage;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class WeUtils {

    private static final String CURRENT_DIR = Paths.get(ValueConsts.DOT_SIGN).toAbsolutePath().normalize().toString();

    private static final String COLOR_PICKER = CURRENT_DIR + File.separator + "ColorPicker.exe";

    private static final String WOX = CURRENT_DIR + File.separator + "Wox.exe";

    private static final String WOX_URL = "https://github.com/Wox-launcher/Wox/releases/latest";

    private static final String WOX_ERROR = LocalValueConsts.CAN_NOT_FOUND + WOX;

    private static final String WOX_TIP = "请下载 Wox-x.x.x.exe 文件到 " + CURRENT_DIR + " 目录，并重命名为：Wox.exe";

    private static final String COLOR_PICKER_ERROR = LocalValueConsts.CAN_NOT_FOUND + COLOR_PICKER;

    private static final String COLOR_PICKER_URL = "http://oq3iwfipo.bkt.clouddn.com/tools/zhazhapan/ColorPicker.exe";

    private static Pattern FILE_FILTER = Pattern.compile(ConfigModel.getFileFilterRegex());

    public static void startColorPicker() {
        if (Checker.isWindows()) {
            ThreadPool.executor.submit(() -> {
                if (Checker.isNotExists(COLOR_PICKER)) {
                    Downloader.download(CURRENT_DIR, COLOR_PICKER_URL);
                }
                if (Checker.isExists(COLOR_PICKER)) {
                    run(COLOR_PICKER);
                } else {
                    Platform.runLater(() -> Alerts.showError(LocalValueConsts.MAIN_TITLE, COLOR_PICKER_ERROR));
                }
            });
        } else {
            Alerts.showWarning(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NOT_SUPPORT);
        }
    }

    public static void startWox() {
        if (Checker.isWindows()) {
            if (Checker.isNotExists(WOX)) {
                openLink(WOX_URL);
                Alerts.showInformation(LocalValueConsts.MAIN_TITLE, WOX_TIP);
            }
            if (Checker.isExists(WOX)) {
                run(WOX);
            } else {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, WOX_ERROR);
            }
        } else {
            Alerts.showWarning(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NOT_SUPPORT);
        }
    }

    public static void run(String executableFile) {
        Platform.runLater(() -> {
            try {
                Runtime.getRuntime().exec(executableFile);
            } catch (IOException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, e.getMessage());
            }
        });
    }

    public static void closeMysqlConnection() {
        WaveController controller = ControllerModel.getWaveController();
        if (Checker.isNotNull(controller)) {
            try {
                if (Checker.isNotNull(controller.statement)) {
                    controller.statement.close();
                }
                if (Checker.isNotNull(controller.connection)) {
                    controller.connection.close();
                }
            } catch (SQLException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, e.getMessage());
            }
        }
    }

    public static void deleteFiles(File file) {
        if (Checker.isNotNull(file)) {
            if (file.isDirectory()) {
                try {
                    FileExecutor.deleteDirectory(file);
                } catch (IOException e) {
                    Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.DELETE_FILE_ERROR);
                }
            } else {
                FileExecutor.deleteFile(file);
            }
        }
    }

    public static String whois(String domain) {
        try {
            return NetUtils.whois(domain);
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NETWORK_ERROR);
            return null;
        }
    }

    public static String getLocationByIp(String ip) {
        try {
            return NetUtils.getLocationByIp(ip);
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NETWORK_ERROR);
            return null;
        }
    }

    public static void putDragFileInTextArea(TextArea textArea, DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            textArea.setText(readFile(files.get(0)));
        }
    }

    public static void mergeFiles(ObservableList<File> fileObservableList, String filter, boolean isDelete) {
        if (Checker.isNotEmpty(fileObservableList)) {
            File file = getSaveFile();
            File[] files = new File[fileObservableList.size()];
            files = fileObservableList.toArray(files);
            try {
                FileExecutor.mergeFiles(files, file, Checker.checkNull(filter));
                if (isDelete) {
                    for (File f : fileObservableList) {
                        f.delete();
                    }
                    fileObservableList.clear();
                }
                showSuccessInfo();
            } catch (IOException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.MERGE_FILE_ERROR);
            }
        }
    }

    public static void removeSelectedItems(ListView<File> fileListView) {
        ObservableList<File> files = fileListView.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            fileListView.getItems().removeAll(files);
        }
    }

    public static void splitFile(File file, long[] points, String folder, boolean deleteSrc) {
        try {
            FileExecutor.splitFile(file, points, folder);
            if (deleteSrc) {
                file.delete();
            }
            showSuccessInfo();
        } catch (IOException e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.SPLIT_FILE_ERROR);
        }
    }

    public static String getFolder(File file) {
        return file.isDirectory() ? file.getAbsolutePath() : file.getParent();
    }

    public static void copyFiles(ObservableList<File> list, String folder, boolean deleteSrc) {
        if (Checker.isNotEmpty(list) && Checker.isNotEmpty(folder)) {
            ThreadPool.executor.submit(() -> {
                File[] files = new File[list.size()];
                list.toArray(files);
                try {
                    FileExecutor.copyFiles(files, folder);
                    if (deleteSrc) {
                        int i = 0;
                        for (File f : list) {
                            list.set(i++, new File(folder + ValueConsts.SEPARATOR + f.getName()));
                            f.delete();
                        }
                    }
                    showSuccessInfo();
                } catch (IOException e) {
                    Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.COPY_FILE_ERROR);
                }
            });
        }
    }

    public static void showSuccessInfo() {
        Alerts.showInformation(LocalValueConsts.MAIN_TITLE, LocalValueConsts.OPERATION_SUCCESS);
    }

    public static String replaceVariable(String s) {
        return Checker.checkNull(s).replaceAll(LocalValueConsts.DATE_VARIABLE, Formatter.dateToString(new Date()))
                .replaceAll(LocalValueConsts.TIME_VARIABLE, Formatter.datetimeToCustomString(new Date(),
                        LocalValueConsts.TIME_FORMAT));
    }

    public static int stringToInt(String integer) {
        int result = Formatter.stringToInt(integer);
        return result > -1 ? result : 0;
    }

    public static double stringToDouble(String digit) {
        double result = Formatter.stringToDouble(digit);
        return result < 0 ? 0 : result;
    }

    @SuppressWarnings("unchecked")
    public static void putFilesInListViewOfFileManagerTab(Object files) {
        FileManagerController fileManagerController = ControllerModel.getFileManagerController();
        if (Checker.isNotNull(fileManagerController)) {
            int idx = fileManagerController.fileManagerTab.getSelectionModel().getSelectedIndex();
            switch (idx) {
                case 0:
                    putFilesInListView(files, fileManagerController.selectedFilesOfRenameTab);
                    break;
                case 1:
                    putFilesInListView(files, fileManagerController.selectedFilesOfCopyTab);
                case 2:
                    File file = null;
                    if (files instanceof File) {
                        file = (File) files;
                    } else if (files instanceof List) {
                        List<File> list = (List<File>) files;
                        if (Checker.isNotEmpty(list)) {
                            file = list.get(0);
                        }
                    }
                    if (Checker.isNotNull(file)) {
                        fileManagerController.splittingFile = file;
                        fileManagerController.fileContent.setText(readFile(file));
                    }
                    break;
                case 3:
                    putFilesInListView(files, fileManagerController.selectedFilesOfMergeTab);
                    break;
                case 4:
                    if (files instanceof File) {
                        fileManagerController.srcFolderOfDeleteTab.setText(getFolder((File) files));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void putFilesInListView(Object files, ListView listView) {
        ObservableList items = listView.getItems();
        if (files instanceof List) {
            putFilesInListView((List<File>) files, items);
        } else if (files instanceof File) {
            putFilesInListView((File) files, items);
        }
    }

    public static void putFilesInListView(List<File> files, ObservableList items) {
        if (Checker.isNotEmpty(files)) {
            files.forEach(file -> putFilesInListView(file, items));
        }
    }

    @SuppressWarnings("unchecked")
    private static void putFilesInListView(File file, ObservableList items) {
        if (Checker.isNotNull(file)) {
            if (FILE_FILTER.matcher(file.getName()).matches()) {
                if (file.isDirectory()) {
                    putFilesInListView((List<File>) FileExecutor.listFiles(file, null, ValueConsts.TRUE), items);
                } else if (!items.contains(file)) {
                    items.add(file);
                }
            } else if (ConfigModel.isFileFilterTip()) {
                Alerts.showInformation(LocalValueConsts.MAIN_TITLE, LocalValueConsts.FILE_NOT_MATCH, file
                        .getAbsolutePath());
            }
        }
    }

    public static void saveFile(File file, String fileContent) {
        if (Checker.isNotEmpty(fileContent)) {
            try {
                FileExecutor.saveFile(file, fileContent);
            } catch (IOException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.SAVE_FILE_ERROR);
            }
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
        return getFileChooser().showSaveDialog(stage);
    }

    public static List<File> getChooseFiles() {
        return getFileChooser().showOpenMultipleDialog(stage);
    }

    public static File getChooseFile() {
        return getFileChooser().showOpenDialog(stage);
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
        closeMysqlConnection();
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
