package com.zhazhapan.util.visual.controller;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.ArrayUtils;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class FileManagerController {

    @FXML
    public ListView<File> selectedFilesOfRenameTab;

    @FXML
    public TextField filePrefixOfRenameTab;

    @FXML
    public TextField filePostfixOfRenameTab;

    @FXML
    public TextField startNumberOfRenameTab;

    @FXML
    public ListView<String> destFilesOfRenameTab;

    @FXML
    public TextField fileQueryStringOfRenameTab;

    @FXML
    public TextField fileReplaceStringOfRenameTab;

    @FXML
    public TextField fileAddableText;

    @FXML
    public ComboBox<String> fileAddableCombo;

    @FXML
    public TextField destFolderOfCopyTab;

    @FXML
    public ListView<File> selectedFilesOfCopyTab;

    @FXML
    public CheckBox isDeleteSrcOfCopyTab;

    @FXML
    public TabPane fileManagerTab;

    @FXML
    public CheckBox isDeleteSrcOfSplitTab;

    @FXML
    public TextField splitPoint;

    @FXML
    public TextArea fileContent;

    @FXML
    public TextField destFolderOfSplitTab;

    @FXML
    public File splittingFile;

    @FXML
    public ListView<File> selectedFilesOfMergeTab;

    @FXML
    public TextField contentFilter;

    @FXML
    public CheckBox deleteSrcOfMergeTab;

    @FXML
    private void initialize() {
        //设置多选
        selectedFilesOfRenameTab.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFilesOfCopyTab.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFilesOfMergeTab.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //设置可编辑
        destFilesOfRenameTab.setCellFactory(TextFieldListCell.forListView());
        destFilesOfRenameTab.setEditable(true);

        fileAddableCombo.getItems().addAll(LocalValueConsts.BEFORE_FILENAME, LocalValueConsts.AFTER_FILENAME);
        fileAddableCombo.getSelectionModel().selectFirst();
        fileAddableCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                generateRenameDestFilesOfAddable());

        fileContent.setWrapText(true);
        ControllerModel.setFileManagerController(this);
    }

    @SuppressWarnings("unchecked")
    public void dragFileDropped(DragEvent event) {
        Object target = event.getGestureTarget();
        if (target instanceof ListView) {
            WeUtils.putFilesInListView(event.getDragboard().getFiles(), ((ListView<File>) target).getItems());
        }
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    public void generateRenameDestFilesOfFormat() {
        ObservableList<File> list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            String startNumber = Checker.checkNull(startNumberOfRenameTab.getText());
            int len = startNumber.length();
            startNumber = startNumber.replaceAll(ValueConsts.SHARP, ValueConsts.EMPTY_STRING);
            int i = WeUtils.stringToInt(startNumber);
            int numLen = len - startNumber.length() + String.valueOf(i).length();
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (File file : list) {
                String postfix = WeUtils.replaceVariable(filePostfixOfRenameTab.getText());
                String prefix = WeUtils.replaceVariable(filePrefixOfRenameTab.getText());
                String fileName = prefix + Formatter.numberFormat(i++, numLen) + (postfix.startsWith(ValueConsts
                        .DOT_SIGN) ? postfix : (Checker.isEmpty(postfix) ? ValueConsts.EMPTY_STRING : ValueConsts
                        .DOT_SIGN + postfix));
                destFiles.add(file.getParent() + ValueConsts.SEPARATOR + fileName);
            }
        }
    }

    public void renameFiles() {
        ObservableList<File> srcFiles = selectedFilesOfRenameTab.getItems();
        ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
        int len = srcFiles.size();
        if (Checker.isNotEmpty(srcFiles) && len == destFiles.size()) {
            for (int i = 0; i < len; i++) {
                File srcFile = srcFiles.get(0);
                File destFile = new File(destFiles.get(i));
                FileExecutor.renameTo(srcFile, destFile);
                srcFiles.remove(srcFile);
                srcFiles.add(destFile);
            }
            WeUtils.showSuccessInfo();
        }
    }

    public void removeFilesFromRenameTab() {
        ObservableList<File> files = selectedFilesOfRenameTab.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList<File> fileList = selectedFilesOfRenameTab.getItems();
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            if (Checker.isNotEmpty(destFiles)) {
                int i = 0;
                for (File file : files) {
                    destFiles.remove(fileList.indexOf(file) - (i++));
                }
            }
            fileList.removeAll(files);
        }
    }

    public void generateRenameDestFilesOfReplace() {
        ObservableList<File> list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (File file : list) {
                String fn = file.getName().replaceAll(Checker.checkNull(fileQueryStringOfRenameTab.getText()),
                        Checker.checkNull(fileReplaceStringOfRenameTab.getText()));
                destFiles.add(file.getParent() + ValueConsts.SEPARATOR + fn);
            }
        }
    }

    public void generateRenameDestFilesOfAddable() {
        ObservableList<File> list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (File file : list) {
                String text = WeUtils.replaceVariable(fileAddableText.getText());
                String fn = file.getName();
                int idx = fn.lastIndexOf(ValueConsts.DOT_SIGN);
                fn = LocalValueConsts.BEFORE_FILENAME.equals(fileAddableCombo.getSelectionModel().getSelectedItem())
                        ? text + fn : fn.substring(0, idx) + text + fn.substring(idx);
                destFiles.add(file.getParent() + ValueConsts.SEPARATOR + fn);
            }
        }
    }

    public void copyFiles() {
        WeUtils.copyFiles(selectedFilesOfCopyTab.getItems(), destFolderOfCopyTab.getText(), isDeleteSrcOfCopyTab
                .isSelected());
    }

    public void browseSrcFolder() {
        File file = WeUtils.getChooseFile();
        if (Checker.isNotNull(file)) {
            int idx = fileManagerTab.getSelectionModel().getSelectedIndex();
            switch (idx) {
                case 1:
                    destFolderOfCopyTab.setText(file.getParent());
                    break;
                case 2:
                    destFolderOfSplitTab.setText(file.getParent());
                default:
                    break;
            }
        }
    }

    public void dragFileDroppedOfCopyTab(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            Object target = event.getGestureTarget();
            if (target instanceof ListView) {
                WeUtils.putFilesInListView(event.getDragboard().getFiles(), selectedFilesOfCopyTab.getItems());
            } else if (target instanceof TextField) {
                destFolderOfCopyTab.setText(WeUtils.getFolder(files.get(0)));
            }
        }
    }

    public void splitFile() {
        String pointStr = splitPoint.getText();
        String folder = destFolderOfSplitTab.getText();
        if (Checker.isNotNull(splittingFile) && Checker.isNotEmpty(pointStr) && Checker.isNotEmpty(folder)) {
            String[] points = pointStr.split(ValueConsts.COMMA_SIGN);
            int len = points.length;
            long[] ps = new long[points.length];
            for (int i = 0; i < len; i++) {
                ps[i] = Formatter.stringToLong(points[i]);
            }
            Arrays.sort(ps);
            ps = ArrayUtils.unique(ps, ValueConsts.ONE_INT, Long.MAX_VALUE);
            WeUtils.splitFile(splittingFile, ps, folder, isDeleteSrcOfSplitTab.isSelected());
        }
    }

    public void dragFileDroppedOfSplitTab(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        if (Checker.isNotEmpty(files)) {
            Object target = event.getGestureTarget();
            File file = files.get(0);
            if (target instanceof TextArea) {
                splittingFile = file;
                fileContent.setText(WeUtils.readFile(file));
            } else if (target instanceof TextField) {
                destFolderOfSplitTab.setText(WeUtils.getFolder(file));
            }
        }
    }

    public void generateSplitPoint() {
        int caret = fileContent.getCaretPosition();
        String points = Checker.checkNull(splitPoint.getText()).trim();
        if (!points.contains(String.valueOf(caret))) {
            splitPoint.setText(points + (Checker.isEmpty(points) || points.endsWith(",") ? caret : "," + caret));
        }
    }

    public void scrollTo() {
        String points = splitPoint.getText();
        if (Checker.isNotEmpty(points) && Checker.isNotEmpty(fileContent.getText())) {
            int position = splitPoint.getCaretPosition();
            String before = points.substring(0, position);
            String after = points.substring(position);
            String point = "";
            if (Checker.isNotEmpty(before)) {
                String[] temp = before.split(ValueConsts.COMMA_SIGN);
                point = temp[temp.length - 1].trim();
            }
            if (Checker.isNotEmpty(after)) {
                point += after.split(ValueConsts.COMMA_SIGN)[0].trim();
            }
            int caret = Formatter.stringToInt(point);
            fileContent.setScrollTop(caret > -1 ? caret : 0);
        }
    }

    public void mergeFiles() {
        WeUtils.mergeFiles(selectedFilesOfMergeTab.getItems(), contentFilter.getText(), deleteSrcOfMergeTab
                .isSelected());
    }

    public void removeFilesOfCopyTab() {
        WeUtils.removeSelectedItems(selectedFilesOfCopyTab);
    }

    public void goForward() {
        ObservableList<File> files = selectedFilesOfMergeTab.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList<File> list = selectedFilesOfMergeTab.getItems();
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
        ObservableList<File> files = selectedFilesOfMergeTab.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList<File> list = selectedFilesOfMergeTab.getItems();
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

    public void removeFilesOfMergeTab() {
        WeUtils.removeSelectedItems(selectedFilesOfMergeTab);
    }
}
