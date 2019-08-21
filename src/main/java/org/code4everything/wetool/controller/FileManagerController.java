package org.code4everything.wetool.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.constant.StringConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.Callable;
import org.code4everything.wetool.util.FxDialogs;
import org.code4everything.wetool.util.FxUtils;
import org.code4everything.wetool.util.WeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pantao
 * @since 2018/3/31
 */
@Slf4j
public class FileManagerController implements BaseViewController {

    private final Map<Object, Callable<List<File>>> dropCallableMap = new HashMap<>(16, 1);

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

    private List<ListView<File>> listViews;

    @FXML
    private void initialize() {
        log.info("open tab for file manager");
        BeanFactory.registerView(TitleConsts.FILE_MANAGER, this);

        //设置多选
        srcFilesOfTabRename.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        srcFilesOfTabCopy.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        srcFilesOfTabMerge.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //设置可编辑
        destFilesOfTabRename.setCellFactory(TextFieldListCell.forListView());
        destFilesOfTabRename.setEditable(true);

        // 设置文件名追加方式
        modeOfTabRename.getItems().addAll(TitleConsts.FILENAME_BEFORE, TitleConsts.FILENAME_AFTER);
        modeOfTabRename.getSelectionModel().selectLast();
        modeOfTabRename.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> generateNewNameForAdding());

        // 添加拖曳文件的处理方法
        dropCallableMap.put(srcFilesOfTabRename, files -> WeUtils.addFiles(srcFilesOfTabRename.getItems(), files));
        dropCallableMap.put(srcFilesOfTabCopy, files -> WeUtils.addFiles(srcFilesOfTabCopy.getItems(), files));
        dropCallableMap.put(srcFilesOfTabMerge, files -> WeUtils.addFiles(srcFilesOfTabMerge.getItems(), files));
        Callable<List<File>> callable = files -> destFolderOfTabCopy.setText(WeUtils.parseFolder(files.get(0)));
        dropCallableMap.put(destFolderOfTabCopy, callable);

        // 按选项卡顺序添加文件视图
        listViews = Lists.newArrayList(srcFilesOfTabRename, srcFilesOfTabCopy, srcFilesOfTabMerge);
    }

    public void generateNewNameForFormatting() {
        List<File> list = srcFilesOfTabRename.getItems();
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
        List<String> destFiles = destFilesOfTabRename.getItems();
        destFiles.clear();
        for (File file : list) {
            if (StrUtil.isEmpty(postfix) || postfix.equals(StringConsts.Sign.DOT)) {
                postfix = "." + FileUtil.extName(file);
            }
            String fileName = prefix + (start++) + postfix;
            destFiles.add(file.getParent() + File.separator + fileName);
        }
    }

    public void renameFiles() {
        List<File> srcFiles = srcFilesOfTabRename.getItems();
        List<String> destFiles = destFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(srcFiles) || srcFiles.size() != destFiles.size()) {
            return;
        }
        for (String destFile : destFiles) {
            File srcFile = srcFiles.get(0);
            FileUtil.rename(srcFile, destFile, false, true);
            log.info("rename file '{}' to '{}'", srcFile.getAbsolutePath(), destFile);
            // 更新源文件名
            srcFiles.remove(srcFile);
            srcFiles.add(new File(destFile));
        }
        FxDialogs.showSuccess();
    }

    public void removeFilesFromTabRename() {
        removeSelectedFiles(srcFilesOfTabRename);
    }

    public void generateNewNameForReplacing() {
        List<File> list = srcFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<String> destFiles = destFilesOfTabRename.getItems();
        destFiles.clear();
        for (File file : list) {
            String filename = file.getName();
            String query = StrUtil.nullToEmpty(queryOfTabRename.getText());
            String replace = WeUtils.replaceVariable(replaceOfTabRename.getText());
            destFiles.add(file.getParent() + File.separator + filename.replaceAll(query, replace));
        }
    }

    public void generateNewNameForAdding() {
        List<File> list = srcFilesOfTabRename.getItems();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<String> destFiles = destFilesOfTabRename.getItems();
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
        List<File> files = srcFilesOfTabCopy.getItems();
        if (CollUtil.isEmpty(files) || StrUtil.isEmpty(destFolderOfTabCopy.getText())) {
            return;
        }
        File folder = new File(destFolderOfTabCopy.getText());
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            FileUtil.copy(file, folder, true);
            log.info("copy file '{}' to folder '{}'", file.getAbsolutePath(), folder.getAbsolutePath());
            if (deleteOfTabCopy.isSelected()) {
                log.info("delete source file: {}", file.getAbsolutePath());
                // 删除源文件
                FileUtil.del(file);
                // 更新源文件的文件名
                files.set(i, new File(folder + File.separator + file.getName()));
            }
        }
        FxDialogs.showSuccess();
    }

    public void chooseFolder() {
        FxUtils.chooseFile(file -> destFolderOfTabCopy.setText(file.getParent()));
    }

    public void mergeFiles() {
        boolean delete = deleteOfTabMerge.isSelected();
        String filter = filterOfTabMerge.getText();
        FxUtils.saveFile(file -> {
            // 创建新文件
            FileUtil.del(file);
            FileUtil.touch(file);
            // 合并
            List<File> files = srcFilesOfTabMerge.getItems();
            for (File f : files) {
                String str = FileUtil.readUtf8String(f);
                if (StrUtil.isNotEmpty(filter)) {
                    // 过滤内容
                    str = str.replaceAll(filter, "");
                }
                // 合并
                FileUtil.appendUtf8String(str, file);
                log.info("merge file '{}' into '{}'", f.getAbsolutePath(), file.getAbsolutePath());
                if (delete) {
                    FileUtil.del(f);
                    log.info("delete source file: {}", f.getAbsolutePath());
                }
            }
            if (delete) {
                // 清空源文件
                files.clear();
            }
        });
    }

    public void removeFilesFromTabCopy() {
        removeSelectedFiles(srcFilesOfTabCopy);
    }

    /**
     * 上移文件
     */
    public void goForward() {
        List<File> files = srcFilesOfTabMerge.getSelectionModel().getSelectedItems();
        if (CollUtil.isEmpty(files)) {
            return;
        }
        List<File> list = srcFilesOfTabMerge.getItems();
        int len = files.size();
        if (len == list.size()) {
            return;
        }
        for (int i = 0; i < len; i++) {
            int idx = list.indexOf(files.get(i));
            if (idx > i) {
                File temp = list.get(idx - 1);
                list.set(idx - 1, files.get(i));
                list.set(idx, temp);
            }
        }
    }

    /**
     * 下移文件
     */
    public void goBack() {
        List<File> files = srcFilesOfTabMerge.getSelectionModel().getSelectedItems();
        if (CollUtil.isEmpty(files)) {
            return;
        }
        List<File> list = srcFilesOfTabMerge.getItems();
        int size = list.size();
        int len = files.size();
        if (len == size) {
            return;
        }
        for (int i = len - 1, j = 1; i >= 0; i--, j++) {
            int idx = list.indexOf(files.get(i));
            if (idx < (size - j)) {
                File temp = list.get(idx + 1);
                list.set(idx + 1, files.get(i));
                list.set(idx, temp);
            }
        }
    }

    public void removeFilesFromTabMerge() {
        removeSelectedFiles(srcFilesOfTabMerge);
    }

    private void removeSelectedFiles(ListView<File> fileListView) {
        List<File> removes = fileListView.getSelectionModel().getSelectedItems();
        if (CollUtil.isEmpty(removes)) {
            return;
        }
        fileListView.getItems().removeAll(removes);
    }

    @Override
    public void dragFileDropped(DragEvent event) {
        FxUtils.dropFiles(event, dropCallableMap);
    }

    @Override
    public void dragFileOver(DragEvent event) {
        FxUtils.acceptCopyMode(event);
    }

    @Override
    public void openMultiFiles(List<File> files) {
        ListView<File> view = listViews.get(fileManagerTab.getSelectionModel().getSelectedIndex());
        WeUtils.addFiles(view.getItems(), files);
    }

    @Override
    public void openFile(File file) {
        openMultiFiles(Lists.newArrayList(file));
    }
}
