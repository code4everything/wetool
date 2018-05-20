package com.zhazhapan.util.visual.controller;

import cn.hutool.core.util.ClipboardUtil;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.qiniu.QiniuApplication;
import com.zhazhapan.qiniu.view.MainWindow;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.ReflectUtils;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author pantao
 * @since 2018/3/30
 */
public class MainController {

    private static Logger logger = Logger.getLogger(MainController.class);
    @FXML
    public TabPane tabPane;

    @FXML
    private void initialize() {
        // 监听剪贴板
        ConfigModel.appendClipboardHistory(new Date(), ClipboardUtil.getStr());
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String clipboard;
                String last;
                try {
                    // 忽略系统休眠时抛出的异常
                    clipboard = ClipboardUtil.getStr();
                    last = ConfigModel.getLastClipboardHistoryItem().getValue();
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                    clipboard = last = ValueConsts.EMPTY_STRING;

                }
                if (Checker.isNotEmpty(clipboard) && !last.equals(clipboard)) {
                    Date date = new Date();
                    ConfigModel.appendClipboardHistory(date, clipboard);
                    ClipboardHistoryController controller = ControllerModel.getClipboardHistoryController();
                    if (Checker.isNotNull(controller)) {
                        final String clip = clipboard;
                        Platform.runLater(() -> controller.insert(date, clip));
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, LocalValueConsts.ONE_THOUSAND, LocalValueConsts.ONE_THOUSAND);
        ControllerModel.setMainController(this);
    }

    public void loadTabs() {
        for (Object tabName : ConfigModel.getTabs()) {
            try {
                ReflectUtils.invokeMethod(this, "open" + tabName + "Tab", null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.FXML_LOAD_ERROR);
            }
        }
    }

    public void quit() {
        WeUtils.exitSystem();
    }

    public void openNetworkToolTab() {
        addTab(new Tab(LocalValueConsts.NETWORK_TOOL), LocalValueConsts.NETWORK_TOOL_VIEW);
    }

    public void openCharsetConverterTab() {
        addTab(new Tab(LocalValueConsts.CHARSET_CONVERTER), LocalValueConsts.CHARSET_CONVERTER_VIEW);
    }

    public void openClipboardHistoryTab() {
        addTab(new Tab(LocalValueConsts.CLIPBOARD_HISTORY), LocalValueConsts.CLIPBOARD_HISTORY_VIEW);
    }

    public void openRandomGeneratorTab() {
        addTab(new Tab(LocalValueConsts.RANDOM_GENERATOR), LocalValueConsts.RANDOM_GENERATOR_VIEW);
    }

    public void openQrCodeGeneratorTab() {
        addTab(new Tab(LocalValueConsts.QR_CODE_GENERATOR), LocalValueConsts.QR_CODE_GENERATOR_VIEW);
    }

    public void openJsonParserTab() {
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
                    case LocalValueConsts.QR_CODE_GENERATOR:
                        QrCodeGeneratorController qrCodeGeneratorController = ControllerModel
                                .getQrCodeGeneratorController();
                        if (Checker.isNotNull(qrCodeGeneratorController)) {
                            qrCodeGeneratorController.content.setText(fileContent);
                        }
                    case LocalValueConsts.CHARSET_CONVERTER:
                        CharsetConverterController charsetConverterController = ControllerModel
                                .getCharsetConverterController();
                        if (Checker.isNotNull(charsetConverterController)) {
                            charsetConverterController.originalContent.setText(fileContent);
                        }
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
                    case LocalValueConsts.FILE_MANAGER:
                        FileManagerController fileManagerController = ControllerModel.getFileManagerController();
                        if (Checker.isNotNull(fileManagerController)) {
                            int idx = fileManagerController.fileManagerTab.getSelectionModel().getSelectedIndex();
                            if (idx == ValueConsts.TWO_INT) {
                                fileContent = fileManagerController.fileContent.getText();
                            }
                        }
                        break;
                    case LocalValueConsts.CLIPBOARD_HISTORY:
                        ClipboardHistoryController clipboardHistoryController = ControllerModel
                                .getClipboardHistoryController();
                        if (Checker.isNotNull(clipboardHistoryController)) {
                            fileContent = clipboardHistoryController.clipboardHistory.getText();
                        }
                        break;
                    case LocalValueConsts.CHARSET_CONVERTER:
                        CharsetConverterController charsetConverterController = ControllerModel
                                .getCharsetConverterController();
                        if (Checker.isNotNull(charsetConverterController)) {
                            fileContent = charsetConverterController.convertedContent.getText();
                        }
                        break;
                    case LocalValueConsts.NETWORK_TOOL:
                        NetworkToolController networkToolController = ControllerModel.getNetworkToolController();
                        if (Checker.isNotNull(networkToolController)) {
                            fileContent = networkToolController.whoisResult.getText();
                        }
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

    public void about() {
        Alerts.showInformation(LocalValueConsts.MAIN_TITLE, LocalValueConsts.ABOUT_APP, LocalValueConsts.ABOUT_DETAIL);
    }

    public void closeAllTab() {
        tabPane.getTabs().clear();
    }

    public void openAllTab() {
        ConfigModel.setTabs(ConfigModel.getSupportTabs());
        loadTabs();
    }

    public void openQiniuToolTab() {
        QiniuApplication.initLoad(ValueConsts.TRUE);
        Tab tab = new Tab(LocalValueConsts.QINIU_TOOL);
        tab.setOnCloseRequest((event) -> MainWindow.setOnClosed(event, ValueConsts.TRUE));
        addTab(tab, LocalValueConsts.QINIU_TOOL_VIEW);
    }

    public void openWaveViewerTab() {
        Tab tab = new Tab(LocalValueConsts.WAVE_VIEWER);
        tab.setOnCloseRequest(event -> WeUtils.closeMysqlConnection());
        addTab(tab, LocalValueConsts.WAVE_VIEW);
    }
}
