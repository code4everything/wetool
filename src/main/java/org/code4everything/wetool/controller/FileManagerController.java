package org.code4everything.wetool.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zhazhapan.util.Checker;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.constant.StringConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;
import org.code4everything.wetool.util.WeUtils;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2018/3/31
 */
@Slf4j
public class FileManagerController implements BaseViewController {

    @FXML
    public ListView<File> srcFilesOfTabRename;

    @FXML
    public TextField prefixOfTabRename;

    @FXML
    public TextField postfixOfTabRename;

    @FXML
    public TextField startOfTabRename;

    @FXML
    public ListView<String> destFilesOfTabRename;

    @FXML
    public TextField queryOfTabRename;

    @FXML
    public TextField replaceOfTabRename;

    @FXML
    public TextField addOfTabRename;

    @FXML
    public ComboBox<String> modeOfTabRename;

    @FXML
    public TextField destFolderOfTabCopy;

    @FXML
    public ListView<File> srcFilesOfTabCopy;

    @FXML
    public CheckBox deleteOfTabCopy;

    @FXML
    public TabPane fileManagerTab;

    @FXML
    public ListView<File> srcFilesOfTabMerge;

    @FXML
    public TextField filterOfTabMerge;

    @FXML
    public CheckBox deleteOfTabMerge;

    @FXML
    private void initialize() {
        BeanFactory.registerView(TitleConsts.FILE_MANAGER, this);
        //设置多选
        srcFilesOfTabRename.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        srcFilesOfTabCopy.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        srcFilesOfTabMerge.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //设置可编辑
        destFilesOfTabRename.setCellFactory(TextFieldListCell.forListView());
        destFilesOfTabRename.setEditable(true);

        modeOfTabRename.getItems().addAll(TitleConsts.FILENAME_BEFORE, TitleConsts.FILENAME_AFTER);
        modeOfTabRename.getSelectionModel().selectLast();
        modeOfTabRename.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> generateNewNameForAdding());
    }

    public void generateNewNameForFormatting() {
        ObservableList<File> list = srcFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 开始索引
        int start = WeUtils.parseInt(startOfTabRename.getText(), 0);
        // 文件前缀
        String prefix = WeUtils.replaceVariable(prefixOfTabRename.getText());
        // 文件后缀
        String postfix = WeUtils.replaceVariable(postfixOfTabRename.getText());
        if (StrUtil.isNotEmpty(postfix) && !postfix.startsWith(StringConsts.Sign.DOT)) {
            postfix = "." + postfix;
        }
        // 目标文件
        ObservableList<String> destFiles = destFilesOfTabRename.getItems();
        destFiles.clear();
        for (File file : list) {
            if (Checker.isEmpty(postfix) || postfix.equals(StringConsts.Sign.DOT)) {
                postfix = "." + FileUtil.extName(file);
            }
            String fileName = prefix + (start++) + postfix;
            destFiles.add(file.getParent() + File.separator + fileName);
        }
    }

    public void renameFiles() {
        ObservableList<File> srcFiles = srcFilesOfTabRename.getItems();
        ObservableList<String> destFiles = destFilesOfTabRename.getItems();
        int len = srcFiles.size();
        if (CollUtil.isEmpty(srcFiles) || len != destFiles.size()) {
            return;
        }
        for (int i = 0; i < len; i++) {
            File srcFile = srcFiles.get(0);
            FileUtil.rename(srcFile, destFiles.get(i), false, true);
            srcFiles.remove(srcFile);
            srcFiles.add(new File(destFiles.get(i)));
        }
        FxUtils.showSuccess();
    }

    public void removeFilesFromTabRename() {
        removeSelectedFiles(srcFilesOfTabRename);
    }

    public void generateNewNameForReplacing() {
        ObservableList<File> list = srcFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        ObservableList<String> destFiles = destFilesOfTabRename.getItems();
        destFiles.clear();
        for (File file : list) {
            String filename = file.getName();
            String query = StrUtil.nullToEmpty(queryOfTabRename.getText());
            String replace = WeUtils.replaceVariable(replaceOfTabRename.getText());
            destFiles.add(file.getParent() + File.separator + filename.replaceAll(query, replace));
        }
    }

    public void generateNewNameForAdding() {
        ObservableList<File> list = srcFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        ObservableList<String> destFiles = destFilesOfTabRename.getItems();
        destFiles.clear();
        for (File file : list) {
            String text = WeUtils.replaceVariable(addOfTabRename.getText());
            String filename = file.getName();
            if (TitleConsts.FILENAME_BEFORE.equals(modeOfTabRename.getSelectionModel().getSelectedItem())) {
                // 文件名之前添加
                filename = text + filename;
            } else {
                // 文件名之后添加
                int idx = filename.lastIndexOf(StringConsts.Sign.DOT);
                filename = filename.substring(0, idx) + text + filename.substring(idx);
            }
            destFiles.add(file.getParent() + File.separator + filename);
        }
    }

    public void copyFiles() {
        ObservableList<File> files = srcFilesOfTabCopy.getItems();
        if (CollUtil.isEmpty(files) || StrUtil.isEmpty(destFolderOfTabCopy.getText())) {
            return;
        }
        File folder = new File(destFolderOfTabCopy.getText());
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            FileUtil.copy(file, folder, true);
            if (deleteOfTabCopy.isSelected()) {
                // 删除源文件
                FileUtil.del(file);
                // 更新源文件的文件名
                files.set(i, new File(folder + File.separator + file.getName()));
            }
        }
        FxUtils.showSuccess();
    }

    public void chooseFolder() {
        FxUtils.chooseFile(file -> destFolderOfTabCopy.setText(file.getParent()));
    }

    public void dragFileDroppedOfCopyTab(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            Object target = event.getGestureTarget();
            if (target instanceof ListView) {
                WeUtils.putFilesInListView(event.getDragboard().getFiles(), srcFilesOfTabCopy.getItems());
            } else if (target instanceof TextField) {
                destFolderOfTabCopy.setText(WeUtils.parseFolder(files.get(0)));
            }
        }
    }

    public void mergeFiles() {
        WeUtils.mergeFiles(srcFilesOfTabMerge.getItems(), filterOfTabMerge.getText(), deleteOfTabMerge.isSelected());
    }

    public void removeFilesFromTabCopy() {
        removeSelectedFiles(srcFilesOfTabCopy);
    }

    public void goForward() {
        ObservableList<File> files = srcFilesOfTabMerge.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList<File> list = srcFilesOfTabMerge.getItems();
            int len = files.size();
            if (len < list.size()) {
                for (int i = 0; i < len; i++) {
                    int idx = list.indexOf(files.get(i));
                    if (idx > i) {
                        File temp = list.get(idx - 1);
                        list.set(idx - 1, files.get(i));
                        list.set(idx, temp);
                    }
                }
            }
        }
    }

    public void goBack() {
        ObservableList<File> files = srcFilesOfTabMerge.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList<File> list = srcFilesOfTabMerge.getItems();
            int size = list.size();
            int len = files.size();
            if (files.size() < size) {
                for (int i = len - 1, j = 0; i >= 0; i--) {
                    int idx = list.indexOf(files.get(i));
                    if (idx < (size - (++j))) {
                        File temp = list.get(idx + 1);
                        list.set(idx + 1, files.get(i));
                        list.set(idx, temp);
                    }
                }
            }
        }
    }

    public void removeFilesFromTabMerge() {
        removeSelectedFiles(srcFilesOfTabMerge);
    }

    private void removeSelectedFiles(ListView<File> fileListView) {
        ObservableList<File> removes = fileListView.getSelectionModel().getSelectedItems();
        if (CollUtil.isEmpty(removes)) {
            return;
        }
        fileListView.getItems().removeAll(removes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dragFileDropped(DragEvent event) {
        Object target = event.getGestureTarget();
        if (target instanceof ListView) {
            WeUtils.putFilesInListView(event.getDragboard().getFiles(), ((ListView<File>) target).getItems());
        }
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
    }

    @Override
    public void openMultiFiles(List<File> files) {
        WeUtils.putFilesInListViewOfFileManagerTab(files);
    }

    @Override
    public void openFile(File file) {
        WeUtils.putFilesInListViewOfFileManagerTab(file);
    }
}
