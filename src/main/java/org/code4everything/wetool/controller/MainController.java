package org.code4everything.wetool.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.constant.IntegerConsts;
import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.config.WeStart;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxDialogs;
import org.code4everything.wetool.util.FxUtils;
import org.code4everything.wetool.util.WeUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class MainController {

    private static final ThreadFactory FACTORY = ThreadFactoryBuilder.create().setDaemon(true).build();

    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1, FACTORY);

    private final Stage stage = BeanFactory.get(Stage.class);

    private final WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    public TabPane tabPane;

    @FXML
    public ProgressBar jvm;

    @FXML
    public Menu fileMenu;

    /**
     * 此对象暂时不注册到工厂
     */
    @FXML
    private void initialize() {
        config.appendClipboardHistory(new Date(), ClipboardUtil.getStr());
        // 监听剪贴板和JVM
        EXECUTOR.scheduleWithFixedDelay(() -> {
            watchClipboard();
            watchJVM();
        }, 0, IntegerConsts.ONE_THOUSAND_AND_TWENTY_FOUR, TimeUnit.MILLISECONDS);
        // 加载快速启动选项
        List<WeStart> starts = BeanFactory.get(WeConfig.class).getQuickStarts();
        if (CollUtil.isNotEmpty(starts)) {
            Menu menu = new Menu(TitleConsts.QUICK_START);
            setQuickStartMenu(menu, starts);
            fileMenu.getItems().add(0, new SeparatorMenuItem());
            fileMenu.getItems().add(0, menu);
        }
        // 加载默认选项卡
        loadTabs();
    }

    private void setQuickStartMenu(Menu menu, List<WeStart> starts) {
        starts.forEach(start -> {
            if (CollUtil.isEmpty(start.getSubStarts())) {
                // 添加子菜单
                MenuItem item = new MenuItem(start.getAlias());
                item.setOnAction(e -> FxUtils.openFile(start.getLocation()));
                menu.getItems().add(item);
            } else {
                // 添加父级菜单
                Menu subMenu = new Menu(start.getAlias());
                menu.getItems().add(subMenu);
                setQuickStartMenu(subMenu, start.getSubStarts());
            }
        });
    }

    private void watchClipboard() {
        String clipboard;
        String last;
        try {
            // 忽略系统休眠时抛出的异常
            clipboard = ClipboardUtil.getStr();
            last = config.getLastClipboardHistoryItem().getValue();
        } catch (Exception e) {
            log.warn(e.getMessage());
            clipboard = last = "";

        }
        if (StrUtil.isEmpty(clipboard) || last.equals(clipboard)) {
            return;
        }
        // 剪贴板发生变化
        String compress = WeUtils.compressString(clipboard);
        log.info("clipboard changed: {}", compress);
        Date date = new Date();
        config.appendClipboardHistory(date, clipboard);
        ClipboardHistoryController controller = BeanFactory.get(ClipboardHistoryController.class);
        if (ObjectUtil.isNotNull(controller)) {
            // 显示到文本框
            final String clip = clipboard;
            Platform.runLater(() -> controller.insert(date, clip));
        }
    }

    private void watchJVM() {
        // 监听JVM内存变化
        if (stage.isShowing() && !stage.isIconified()) {
            Platform.runLater(() -> {
                double total = Runtime.getRuntime().totalMemory();
                double used = total - Runtime.getRuntime().freeMemory();
                jvm.setProgress(used / total);
            });
        }
    }

    private void loadTabs() {
        for (Object tabName : config.getInitialize().getTabs().getLoads()) {
            ReflectUtil.invoke(this, "open" + tabName + "Tab");
        }
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

    public void openNaryConverterTab() {
        openTab(TitleConsts.NARY_CONVERTER, ViewConsts.NARY_CONVERTER);
    }

    private void openTab(String tabName, String url) {
        List<Tab> tabs = tabPane.getTabs();
        for (Tab t : tabs) {
            if (t.getText().equals(tabName)) {
                // 选项卡已打开，退出方法
                tabPane.getSelectionModel().select(t);
                return;
            }
        }
        VBox box = FxUtils.loadFxml(url);
        if (Objects.isNull(box)) {
            return;
        }
        // 打开选项卡
        Tab tab = new Tab(tabName);
        tab.setContent(box);
        tab.setClosable(true);
        tabs.add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void openFile() {
        FxUtils.chooseFile(file -> {
            BaseViewController controller = getCurrentTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openFile(file);
            }
        });
    }

    public void saveFile() {
        FxUtils.saveFile(file -> {
            BaseViewController controller = getCurrentTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.saveFile(file);
            }
        });
    }

    public void openMultiFile() {
        FxUtils.chooseFiles(files -> {
            BaseViewController controller = getCurrentTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openMultiFiles(files);
            }
        });
    }

    private BaseViewController getCurrentTabController() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        return Objects.isNull(tab) ? null : BeanFactory.getView(tab.getText());
    }

    public void quit() {
        WeUtils.exitSystem();
    }

    public void about() {
        FxDialogs.showInformation(TitleConsts.ABOUT_APP, TipConsts.ABOUT_APP);
    }

    public void closeAllTab() {
        tabPane.getTabs().clear();
    }

    public void openAllTab() {
        config.getInitialize().getTabs().setLoads(config.getInitialize().getTabs().getSupports());
        loadTabs();
    }

    public void openLog() {
        FxUtils.openFile(StrUtil.join(File.separator, FileUtil.getUserHomePath(), "logs", "wetool", "wetool.log"));
    }

    public void restart() {
        FxUtils.restart();
    }

    public void openConfig() {
        FxUtils.openFile(BeanFactory.get(WeConfig.class).getCurrentPath());
    }
}
