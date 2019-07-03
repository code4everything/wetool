package org.code4everything.wetool.controller;

import cn.hutool.core.swing.ClipboardUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.ReflectUtils;
import com.zhazhapan.util.dialog.Alerts;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.code4everything.wetool.util.WeUtils;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ValueConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.factor.BeanFactory;
import org.code4everything.wetool.model.ConfigModel;

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

    private final Stage stage = BeanFactory.get(Stage.class);

    @FXML
    public TabPane tabPane;

    @FXML
    public ProgressBar jvm;

    @FXML
    private void initialize() {
        BeanFactory.register(this);
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
                    clipboard = last = com.zhazhapan.modules.constant.ValueConsts.EMPTY_STRING;

                }
                if (Checker.isNotEmpty(clipboard) && !last.equals(clipboard)) {
                    Date date = new Date();
                    ConfigModel.appendClipboardHistory(date, clipboard);
                    ClipboardHistoryController controller = BeanFactory.get(ClipboardHistoryController.class);
                    if (Checker.isNotNull(controller)) {
                        final String clip = clipboard;
                        Platform.runLater(() -> controller.insert(date, clip));
                    }
                }
                boolean isVisible = stage.isShowing() && !stage.isMaximized() && !stage.isIconified();
                // 监听JVM内存变化
                if (isVisible) {
                    Platform.runLater(() -> {
                        double total = Runtime.getRuntime().totalMemory();
                        double used = total - Runtime.getRuntime().freeMemory();
                        jvm.setProgress(used / total);
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, ValueConsts.ONE_THOUSAND, ValueConsts.ONE_THOUSAND);
    }

    public void loadTabs() {
        for (Object tabName : ConfigModel.getTabs()) {
            try {
                ReflectUtils.invokeMethod(this, "open" + tabName + "Tab", null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Alerts.showError(TitleConsts.APP_TITLE, TipConsts.FXML_ERROR);
            }
        }
    }

    public void quit() {
        WeUtils.exitSystem();
    }

    public void openNetworkToolTab() {
        openTab(TitleConsts.NETWORK_TOOL, ViewConsts.NETWORK_TOOL);
    }

    public void openCharsetConverterTab() {
        openTab(TitleConsts.CHARSET_CONVERTER, ViewConsts.CHARSET_CONVERTER);
    }

    public void openClipboardHistoryTab() {
        openTab(TitleConsts.CLIPBOARD_HISTORY, ViewConsts.CLIPBOARD_HISTORY);
    }

    public void openRandomGeneratorTab() {
        openTab(TitleConsts.RANDOM_GENERATOR, ViewConsts.RANDOM_GENERATOR);
    }

    public void openQrCodeGeneratorTab() {
        openTab(TitleConsts.QR_CODE_GENERATOR, ViewConsts.QR_CODE_GENERATOR);
    }

    public void openJsonParserTab() {
        openTab(TitleConsts.JSON_PARSER, ViewConsts.JSON_PARSER);
    }

    public void openFileManagerTab() {
        openTab(TitleConsts.FILE_MANAGER, ViewConsts.FILE_MANAGER);
    }

    private void openTab(String tabName, String url) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab t : tabs) {
            if (t.getText().equals(tabName)) {
                // 选项卡已打开，退出方法
                tabPane.getSelectionModel().select(t);
                return;
            }
        }
        VBox box = WeUtils.loadFxml(url);
        if (Checker.isNull(box)) {
            Alerts.showError(com.zhazhapan.modules.constant.ValueConsts.ERROR, TipConsts.FXML_ERROR);
        } else {
            Tab tab = new Tab(tabName);
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
                    case TitleConsts.JSON_PARSER:
                        JsonParserController controller = BeanFactory.get(JsonParserController.class);
                        if (Checker.isNotNull(controller)) {
                            controller.jsonContent.setText(fileContent);
                        }
                        break;
                    case TitleConsts.FILE_MANAGER:
                        WeUtils.putFilesInListViewOfFileManagerTab(file);
                        break;
                    case TitleConsts.QR_CODE_GENERATOR:
                        QrCodeGeneratorController qrCodeGeneratorController =
                                BeanFactory.get(QrCodeGeneratorController.class);
                        if (Checker.isNotNull(qrCodeGeneratorController)) {
                            qrCodeGeneratorController.content.setText(fileContent);
                        }
                        break;
                    case TitleConsts.CHARSET_CONVERTER:
                        CharsetConverterController charsetConverterController =
                                BeanFactory.get(CharsetConverterController.class);
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
                    case TitleConsts.JSON_PARSER:
                        JsonParserController jsonParserController = BeanFactory.get(JsonParserController.class);
                        if (Checker.isNotNull(jsonParserController)) {
                            fileContent = jsonParserController.parsedJsonContent.getText();
                        }
                        break;
                    case TitleConsts.FILE_MANAGER:
                        FileManagerController fileManagerController = BeanFactory.get(FileManagerController.class);
                        if (Checker.isNotNull(fileManagerController)) {
                            int idx = fileManagerController.fileManagerTab.getSelectionModel().getSelectedIndex();
                            if (idx == com.zhazhapan.modules.constant.ValueConsts.TWO_INT) {
                                fileContent = fileManagerController.fileContent.getText();
                            }
                        }
                        break;
                    case TitleConsts.CLIPBOARD_HISTORY:
                        ClipboardHistoryController clipboardHistoryController =
                                BeanFactory.get(ClipboardHistoryController.class);
                        if (Checker.isNotNull(clipboardHistoryController)) {
                            fileContent = clipboardHistoryController.clipboardHistory.getText();
                        }
                        break;
                    case TitleConsts.CHARSET_CONVERTER:
                        CharsetConverterController charsetConverterController =
                                BeanFactory.get(CharsetConverterController.class);
                        if (Checker.isNotNull(charsetConverterController)) {
                            fileContent = charsetConverterController.convertedContent.getText();
                        }
                        break;
                    case TitleConsts.NETWORK_TOOL:
                        NetworkToolController networkToolController = BeanFactory.get(NetworkToolController.class);
                        if (Checker.isNotNull(networkToolController)) {
                            fileContent = networkToolController.whoisResult.getText();
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

    public void openMultiFile() {
        List<File> files = WeUtils.getChooseFiles();
        if (Checker.isNotEmpty(files)) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (Checker.isNotNull(tab)) {
                String tabText = Checker.checkNull(tab.getText());
                switch (tabText) {
                    case TitleConsts.FILE_MANAGER:
                        WeUtils.putFilesInListViewOfFileManagerTab(files);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void about() {
        Alerts.showInformation(TitleConsts.APP_TITLE, TitleConsts.ABOUT_APP, TipConsts.ABOUT_APP);
    }

    public void closeAllTab() {
        tabPane.getTabs().clear();
    }

    public void openAllTab() {
        ConfigModel.setTabs(ConfigModel.getSupportTabs());
        loadTabs();
    }
}
