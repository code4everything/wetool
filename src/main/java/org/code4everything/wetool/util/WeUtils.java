package org.code4everything.wetool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.NetUtils;
import com.zhazhapan.util.ThreadPool;
import com.zhazhapan.util.dialog.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.constant.IntegerConsts;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.controller.FileManagerController;
import org.code4everything.wetool.factory.BeanFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author pantao
 * @since 2018/3/31
 */
@Slf4j
@UtilityClass
public class WeUtils {

    public static final String TIME_VARIABLE = "%(TIME|time)%";

    public static final String DATE_VARIABLE = "%(DATE|date)%";

    private static WeConfig config;

    private static WeConfig getConfig() {
        if (Objects.isNull(config)) {
            config = BeanFactory.get(WeConfig.class);
        }
        return config;
    }

    public static boolean isWindows() {
        return SystemUtil.getOsInfo().getName().startsWith("Window");
    }

    public static void deleteFiles(File file) {
        if (Checker.isNotNull(file)) {
            if (file.isDirectory()) {
                try {
                    FileExecutor.deleteDirectory(file);
                } catch (IOException e) {
                    Alerts.showError(TitleConsts.APP_TITLE, TipConsts.DELETE_FILE_ERROR);
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
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.NETWORK_ERROR);
            return null;
        }
    }

    public static String getLocationByIp(String ip) {
        try {
            return NetUtils.getLocationByIp(ip);
        } catch (Exception e) {
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.NETWORK_ERROR);
            return null;
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
                Alerts.showError(TitleConsts.APP_TITLE, TipConsts.MERGE_FILE_ERROR);
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
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.SPLIT_FILE_ERROR);
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
                            list.set(i++,
                                     new File(folder + com.zhazhapan.modules.constant.ValueConsts.SEPARATOR + f.getName()));
                            f.delete();
                        }
                    }
                    showSuccessInfo();
                } catch (IOException e) {
                    Alerts.showError(TitleConsts.APP_TITLE, TipConsts.COPY_FILE_ERROR);
                }
            });
        }
    }

    public static void showSuccessInfo() {
        Alerts.showInformation(TitleConsts.APP_TITLE, TipConsts.OPERATION_SUCCESS);
    }

    public static String replaceVariable(String s) {
        s = StrUtil.nullToEmpty(s);
        if (StrUtil.isNotEmpty(s)) {
            Date date = new Date();
            s = s.replaceAll(DATE_VARIABLE, DateUtil.formatDate(date));
            s = s.replaceAll(TIME_VARIABLE, DateUtil.formatTime(date));
        }
        return s;
    }

    public static int parseInt(String num, int minVal) {
        int n = 0;
        if (NumberUtil.isNumber(num)) {
            n = NumberUtil.parseInt(num);
        }
        return Math.max(n, minVal);
    }

    @SuppressWarnings("unchecked")
    public static void putFilesInListViewOfFileManagerTab(Object files) {
        FileManagerController fileManagerController = BeanFactory.get(FileManagerController.class);
        if (Checker.isNotNull(fileManagerController)) {
            int idx = fileManagerController.fileManagerTab.getSelectionModel().getSelectedIndex();
            switch (idx) {
                case 0:
                    putFilesInListView(files, fileManagerController.selectedFilesOfRenameTab);
                    break;
                case 1:
                    putFilesInListView(files, fileManagerController.selectedFilesOfCopyTab);
                    break;
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
            if (getConfig().getFileFilter().getFilterPattern().matcher(file.getName()).matches()) {
                if (file.isDirectory()) {
                    putFilesInListView((List<File>) FileExecutor.listFiles(file, null,
                                                                           com.zhazhapan.modules.constant.ValueConsts.TRUE), items);
                } else if (!items.contains(file)) {
                    items.add(file);
                }
            } else if (getConfig().getFileFilter().getShowTip()) {
                Alerts.showInformation(TitleConsts.APP_TITLE, TipConsts.FILE_NOT_MATCH_ERROR, file.getAbsolutePath());
            }
        }
    }

    public static void saveFile(File file, String fileContent) {
        if (Checker.isNotEmpty(fileContent)) {
            try {
                FileExecutor.saveFile(file, fileContent);
            } catch (IOException e) {
                Alerts.showError(TitleConsts.APP_TITLE, TipConsts.SAVE_FILE_ERROR);
            }
        }
    }

    public static String readFile(File file) {
        String result;
        try {
            result = FileExecutor.readFile(file);
        } catch (IOException e) {
            result = "";
            Alerts.showError(TitleConsts.APP_TITLE, TipConsts.READ_FILE_ERROR);
        }
        return result;
    }

    public static File getSaveFile() {
        return getFileChooser().showSaveDialog(BeanFactory.get(Stage.class));
    }

    public static List<File> getChooseFiles() {
        return getFileChooser().showOpenMultipleDialog(BeanFactory.get(Stage.class));
    }

    public static File getChooseFile() {
        return getFileChooser().showOpenDialog(BeanFactory.get(Stage.class));
    }

    private static FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(TitleConsts.APP_TITLE);
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return chooser;
    }

    public static void exitSystem() {
        log.info("quit application......");
        System.exit(IntegerConsts.ZERO);
    }

    public static VBox loadFxml(String url) {
        try {
            return FXMLLoader.load(WeUtils.class.getResource(url));
        } catch (Exception e) {
            Alerts.showException(com.zhazhapan.modules.constant.ValueConsts.FATAL_ERROR, e);
            return null;
        }
    }
}
