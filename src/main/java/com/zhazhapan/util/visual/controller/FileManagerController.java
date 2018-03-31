package com.zhazhapan.util.visual.controller;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.io.File;

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

    @SuppressWarnings("unchecked")
    @FXML
    private void initialize() {
        selectedFilesOfRenameTab.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        destFilesOfRenameTab.setCellFactory(TextFieldListCell.forListView());
        destFilesOfRenameTab.setEditable(true);
        fileAddableCombo.getItems().addAll(LocalValueConsts.BEFORE_FILENAME, LocalValueConsts.AFTER_FILENAME);
        fileAddableCombo.getSelectionModel().selectFirst();
        fileAddableCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                generateRenameDestFilesOfAddable());
        ControllerModel.setFileManagerController(this);
    }

    public void dragFileDropped(DragEvent event) {
        WeUtils.putFilesInListView(event.getDragboard().getFiles(), selectedFilesOfRenameTab.getItems());
    }

    public void dragFileOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY);
    }

    public void generateRenameDestFilesOfFormat() {
        ObservableList list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            String startNumber = Checker.checkNull(startNumberOfRenameTab.getText());
            int len = startNumber.length();
            startNumber = startNumber.replaceAll(ValueConsts.SHARP, ValueConsts.EMPTY_STRING);
            int i = WeUtils.stringToInt(startNumber);
            int numLen = len - startNumber.length() + String.valueOf(i).length();
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (Object file : list) {
                String postfix = WeUtils.replaceVariable(filePostfixOfRenameTab.getText());
                String prefix = WeUtils.replaceVariable(filePrefixOfRenameTab.getText());
                String fileName = prefix + Formatter.numberFormat(i++, numLen) + (postfix.startsWith(ValueConsts
                        .DOT_SIGN) ? postfix : (Checker.isEmpty(postfix) ? ValueConsts.EMPTY_STRING : ValueConsts
                        .DOT_SIGN + postfix));
                destFiles.add(((File) file).getParent() + ValueConsts.SEPARATOR + fileName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void renameFiles() {
        ObservableList srcFiles = selectedFilesOfRenameTab.getItems();
        ObservableList destFiles = destFilesOfRenameTab.getItems();
        int len = srcFiles.size();
        for (int i = 0; i < len; i++) {
            File srcFile = (File) srcFiles.get(0);
            File destFile = new File((String) destFiles.get(i));
            srcFile.renameTo(destFile);
            srcFiles.remove(srcFile);
            srcFiles.add(destFile);
        }
    }

    public void removeFilesFromRenameTab() {
        ObservableList files = selectedFilesOfRenameTab.getSelectionModel().getSelectedItems();
        if (Checker.isNotEmpty(files)) {
            ObservableList fileList = selectedFilesOfRenameTab.getItems();
            ObservableList destFiles = destFilesOfRenameTab.getItems();
            int len = files.size();
            for (int i = 0; i < len; i++) {
                destFiles.remove(fileList.indexOf(files.get(0)));
                fileList.remove(files.get(0));
            }
        }
    }

    public void generateRenameDestFilesOfReplace() {
        ObservableList list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (Object file : list) {
                File f = (File) file;
                String fn = f.getName().replaceAll(Checker.checkNull(fileQueryStringOfRenameTab.getText()), Checker
                        .checkNull(fileReplaceStringOfRenameTab.getText()));
                destFiles.add(f.getParent() + ValueConsts.SEPARATOR + fn);
            }
        }
    }

    public void generateRenameDestFilesOfAddable() {
        ObservableList list = selectedFilesOfRenameTab.getItems();
        if (Checker.isNotEmpty(list)) {
            ObservableList<String> destFiles = destFilesOfRenameTab.getItems();
            destFiles.clear();
            for (Object file : list) {
                String text = WeUtils.replaceVariable(fileAddableText.getText());
                File f = (File) file;
                int idx = f.getName().lastIndexOf(ValueConsts.DOT_SIGN);
                String fn = LocalValueConsts.BEFORE_FILENAME.equals(fileAddableCombo.getSelectionModel()
                        .getSelectedItem()) ? text + f.getName() : f.getName().substring(0, idx) + text + f.getName()
                        .substring(idx);
                destFiles.add(f.getParent() + ValueConsts.SEPARATOR + fn);
            }
        }
    }
}
