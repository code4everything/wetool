package com.zhazhapan.util.visual.controller;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2018/3/30
 */
public class MainController {

    @FXML
    public TabPane tabPane;

    @FXML
    private void initialize() {
        openParseJsonTab();
        openFileManagerTab();
    }

    public void quit() {
        WeUtils.exitSystem();
    }

    public void openParseJsonTab() {
        addTab(new Tab(LocalValueConsts.JSON_PARSER), LocalValueConsts.JSON_PARSER_VIEW);
    }

    private void addTab(Tab tab, String url) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab t : tabs) {
            if (t.getText().equals(tab.getText())) {
                tabPane.getSelectionModel().select(t);
                return;
            }
        }
        VBox box = WeUtils.loadFxml(url);
        if (Checker.isNull(box)) {
            Alerts.showError(ValueConsts.ERROR, LocalValueConsts.FXML_LOAD_ERROR);
        } else {
            tab.setContent(box);
            tab.setClosable(true);
            tabs.add(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    public void openFile() {
        File file = WeUtils.getChooseFile();
        if (Checker.isNotNull(file)) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (Checker.isNotNull(tab)) {
                String tabText = Checker.checkNull(tab.getText());
                String fileContent = WeUtils.readFile(file);
                switch (tabText) {
                    case LocalValueConsts.JSON_PARSER:
                        JsonParserController controller = ControllerModel.getJsonParserController();
                        if (Checker.isNotNull(controller)) {
                            controller.jsonContent.setText(fileContent);
                        }
                        break;
                    case LocalValueConsts.FILE_MANAGER:
                        WeUtils.putFilesInListViewOfFileManagerTab(file);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void saveFile() {
        File file = WeUtils.getSaveFile();
        if (Checker.isNotNull(file)) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (Checker.isNotNull(tab)) {
                String tabText = Checker.checkNull(tab.getText());
                String fileContent = null;
                switch (tabText) {
                    case LocalValueConsts.JSON_PARSER:
                        JsonParserController jsonParserController = ControllerModel.getJsonParserController();
                        if (Checker.isNotNull(jsonParserController)) {
                            fileContent = jsonParserController.parsedJsonContent.getText();
                        }
                        break;
                    default:
                        break;
                }
                if (Checker.isNotEmpty(fileContent)) {
                    WeUtils.saveFile(file, fileContent);
                }
            }
        }
    }

    public void openFileManagerTab() {
        addTab(new Tab(LocalValueConsts.FILE_MANAGER), LocalValueConsts.FILE_MANAGER_VIEW);
    }

    public void openMultiFile() {
        List<File> files = WeUtils.getChooseFiles();
        if (Checker.isNotEmpty(files)) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (Checker.isNotNull(tab)) {
                String tabText = Checker.checkNull(tab.getText());
                switch (tabText) {
                    case LocalValueConsts.FILE_MANAGER:
                        WeUtils.putFilesInListViewOfFileManagerTab(files);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
