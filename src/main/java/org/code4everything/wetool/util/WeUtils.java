package org.code4everything.wetool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.dialog.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
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

    private static final String TIME_VARIABLE = "%(TIME|time)%";

    private static final String DATE_VARIABLE = "%(DATE|date)%";

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

    public static void mergeFiles(ObservableList<File> fileObservableList, String filter, boolean isDelete) {
        if (Checker.isNotEmpty(fileObservableList)) {
            //            File file = FxUtils.saveFile();
            //            File[] files = new File[fileObservableList.size()];
            //            files = fileObservableList.toArray(files);
            //            try {
            //                FileExecutor.mergeFiles(files, file, Checker.checkNull(filter));
            //                if (isDelete) {
            //                    for (File f : fileObservableList) {
            //                        f.delete();
            //                    }
            //                    fileObservableList.clear();
            //                }
            //                FxUtils.showSuccess();
            //            } catch (IOException e) {
            //                Alerts.showError(TitleConsts.APP_TITLE, TipConsts.MERGE_FILE_ERROR);
            //            }
        }
    }

    public static String parseFolder(File file) {
        return file.isDirectory() ? file.getAbsolutePath() : file.getParent();
    }

    public static String replaceVariable(String str) {
        str = StrUtil.nullToEmpty(str);
        if (StrUtil.isNotEmpty(str)) {
            Date date = new Date();
            str = str.replaceAll(DATE_VARIABLE, DateUtil.formatDate(date));
            str = str.replaceAll(TIME_VARIABLE, DateUtil.formatTime(date));
        }
        return str;
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
                    putFilesInListView(files, fileManagerController.srcFilesOfTabRename);
                    break;
                case 1:
                    putFilesInListView(files, fileManagerController.srcFilesOfTabCopy);
                    break;
                case 2:
                    putFilesInListView(files, fileManagerController.srcFilesOfTabMerge);
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
