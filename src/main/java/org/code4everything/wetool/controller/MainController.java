package org.code4everything.wetool.controller;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.*;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.system.SystemUtil;
import com.google.common.base.Preconditions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.boot.base.constant.StringConsts;
import org.code4everything.wetool.WeApplication;
import org.code4everything.wetool.constant.FileConsts;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.constant.ViewConsts;
import org.code4everything.wetool.handler.MouseMotionEventHandler;
import org.code4everything.wetool.plugin.PluginLoader;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.config.WeStart;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.handler.BaseMouseCornerEventHandler;
import org.code4everything.wetool.plugin.support.event.handler.BaseNoMessageEventHandler;
import org.code4everything.wetool.plugin.support.event.message.ClipboardChangedEventMessage;
import org.code4everything.wetool.plugin.support.event.message.MouseCornerEventMessage;
import org.code4everything.wetool.plugin.support.event.message.QuickStartEventMessage;
import org.code4everything.wetool.plugin.support.exception.ToDialogException;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.func.FunctionCenter;
import org.code4everything.wetool.plugin.support.func.MethodCallback;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.service.HttpFileBrowserService;
import org.code4everything.wetool.service.UpdateService;
import org.code4everything.wetool.util.FinalUtils;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author pantao
 * @since 2018/3/30
 */
@Slf4j
public class MainController {

    private static final Map<String, Pair<String, String>> TAB_MAP = new HashMap<>(16);

    /**
     * key以*结尾时，采用模式匹配，即仅匹配前缀
     */
    private static final Map<String, EventHandler<ActionEvent>> ACTION_MAP = new ConcurrentHashMap<>();

    private static final Map<String, String> ACTION_NAME_PINYIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String, WebView> WEB_TOOL_BROWSER_MAP = new ConcurrentHashMap<>();

    private static final Pattern URL_PATTERN = Pattern.compile("^[a-z]+://.+$");

    private static final Pattern HUTOOL_CMD_PATTERN = Pattern.compile("^[a-z\\-]+.*[^\\s]$");

    static {
        registerAction("FileManager", TitleConsts.FILE_MANAGER, ViewConsts.FILE_MANAGER);
        registerAction("JsonParser", TitleConsts.JSON_PARSER, ViewConsts.JSON_PARSER);
        registerAction("RandomGenerator", TitleConsts.RANDOM_GENERATOR, ViewConsts.RANDOM_GENERATOR);
        registerAction("ClipboardHistory", TitleConsts.CLIPBOARD_HISTORY, ViewConsts.CLIPBOARD_HISTORY);
        registerAction("QrCodeGenerator", TitleConsts.QR_CODE_GENERATOR, ViewConsts.QR_CODE_GENERATOR);
        registerAction("CharsetConverter", TitleConsts.CHARSET_CONVERTER, ViewConsts.CHARSET_CONVERTER);
        registerAction("NetworkTool", TitleConsts.NETWORK_TOOL, ViewConsts.NETWORK_TOOL);
        registerAction("NaryConverter", TitleConsts.NARY_CONVERTER, ViewConsts.NARY_CONVERTER);
    }

    private final Cache<String, EventHandler<ActionEvent>> actionCache = CacheUtil.newFIFOCache(128);

    private final WeConfig config = WeUtils.getConfig();

    private final EventHandler<ActionEvent> hutoolCmdHandler = this::runHutoolCmd;

    @FXML
    public TabPane tabPane;

    @FXML
    public Menu fileMenu;

    @FXML
    public Menu toolMenu;

    @FXML
    public Menu pluginMenu;

    @FXML
    public ComboBox<String> toolSearchBox;

    @FXML
    public TextField hiddenControl;

    @FXML
    public HBox titleBar;

    private WebView webView;

    private static void registerAction(String name, String title, String viewUrl) {
        TAB_MAP.put(name, new Pair<>(title, viewUrl));
        registerAction(title + "/" + name, actionEvent -> {
            Pane box = FxUtils.loadFxml(WeApplication.class, viewUrl, true);
            FinalUtils.openTab(box, title);
        });
    }

    public static void registerAction(String name, EventHandler<ActionEvent> eventHandler) {
        name = StrUtil.trim(name);
        Preconditions.checkArgument(StrUtil.isNotBlank(name), "action name must not be blank");
        ACTION_MAP.put(name, eventHandler);
        if (name.endsWith(StringConsts.Sign.STAR)) {
            return;
        }
        String pinyin = PinyinUtil.getPinyin(name);
        ACTION_NAME_PINYIN_MAP.put(name, StrUtil.cleanBlank(pinyin));
    }

    public static void unregisterAction(String name) {
        name = StrUtil.trim(name);
        ACTION_MAP.remove(name);
        String pinyin = PinyinUtil.getPinyin(name);
        ACTION_NAME_PINYIN_MAP.remove(pinyin);
    }

    /**
     * 此对象暂时不注册到工厂
     */
    @FXML
    private void initialize() {
        BeanFactory.register(tabPane);
        BeanFactory.register(AppConsts.BeanKey.PLUGIN_MENU, pluginMenu);
        BeanFactory.register(AppConsts.BeanKey.TITLE_BAR, titleBar);
        hiddenControl.focusedProperty().addListener((observableValue, aBoolean, t1) -> hiddenControl.setText(StrUtil.EMPTY));
        registerGlobalShortcuts();
        registerShortcuts();

        // 加载快速启动选项
        Set<WeStart> starts = config.getQuickStarts();
        if (CollUtil.isNotEmpty(starts)) {
            Menu menu = new Menu(TitleConsts.QUICK_START);
            setQuickStartMenu(menu, starts);
            fileMenu.getItems().add(0, new SeparatorMenuItem());
            fileMenu.getItems().add(0, menu);
        }

        // 加载工具选项卡
        loadToolMenus(toolMenu);
        // 加载默认选项卡
        loadTabs();
        // 加载网页工具
        addWebTool();
        // 监听剪贴板
        config.appendClipboardHistory(new Date(), ClipboardUtil.getStr());
        EventCenter.subscribeEvent(EventCenter.EVENT_SECONDS_TIMER, new BaseNoMessageEventHandler() {
            @Override
            public void handleEvent0(String s, Date date) {
                watchClipboard(date);
            }
        });

        // 监听鼠标位置
        EventCenter.subscribeEvent(EventCenter.EVENT_MOUSE_MOTION, new MouseMotionEventHandler());
        multiDesktopOnWindows();

        registerActions();
        WeUtils.execute(PluginLoader::loadPlugins);
    }

    private void registerActions() {
        // 注册搜索动作
        registerAction("退出-exit", actionEvent -> WeUtils.exitSystem());
        registerAction("重启-restart", actionEvent -> FxUtils.restart());
        registerAction("打开配置文件-openconfig", actionEvent -> openConfig());
        registerAction("打开日志目录-openlogfolder", actionEvent -> openLogFolder());
        registerAction("打开工作目录-openworkfolder", actionEvent -> openWorkFolder());
        registerAction("javaproperties", actionEvent -> seeJavaInfo());
        registerAction("插件面板-pluginpane", actionEvent -> pluginPane());
        registerAction("检查更新-checkforupdate", actionEvent -> checkUpdate());
        registerAction("隐藏-hide", actionEvent -> FxUtils.hideStage());
        registerAction("插件仓库-pluginrepository", actionEvent -> FxUtils.openLink(TipConsts.REPO_LINK));

        // 注册模式匹配动作
        registerAction("hutool *", hutoolCmdHandler);
        registerAction("file-browser *", HttpFileBrowserService.getInstance());
        registerAction("env *", a -> {
            String name = StrUtil.removePrefix(a.getSource().toString(), "env").trim();
            FxDialogs.showInformation(StrUtil.format("{} 的环境变量", name), System.getenv(name));
        });
        registerAction("go *", actionEvent -> {
            String url = StrUtil.removePrefix(actionEvent.getSource().toString(), "go").trim();
            if (StrUtil.isEmpty(url)) {
                url = ClipboardUtil.getStr();
            }
            if (StrUtil.isNotBlank(url)) {
                if (!URL_PATTERN.matcher(url).matches()) {
                    url = "http://" + url;
                }
                getWebView().getEngine().load(url);
            }
            FinalUtils.openTab(getWebView(), "浏览器");
        });

        FunctionCenter.registerFunc(new MethodCallback() {
            @Override
            public String getUniqueMethodName() {
                return "execute-wetool-action";
            }

            @Override
            public String getDescription() {
                return "执行工具命令";
            }

            @Override
            public List<Class<?>> getParamTypes() {
                return List.of(String.class);
            }

            @Override
            public Object callMethod(List<Object> list) {
                if (CollUtil.isEmpty(list)) {
                    return false;
                }

                Object object = list.get(0);
                String keyword = ObjectUtil.toString(object);
                EventHandler<ActionEvent> eventHandler = getActionEventEventHandler(keyword, "");
                if (Objects.isNull(eventHandler)) {
                    return false;
                }

                eventHandler.handle(new ActionEvent(keyword, null));
                return true;
            }

            @Override
            public Object checkAndCallMethod(List<Object> params) {
                return callMethod(params);
            }
        });
    }

    private WebView getWebView() {
        if (Objects.isNull(webView)) {
            webView = newWebView();
        }
        return webView;
    }

    private WebView newWebView() {
        WebView view = new WebView();
        WebEngine engine = view.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
        return view;
    }

    private void addWebTool() {
        LinkedHashMap<String, String> webTools = config.getWebTools();
        if (Objects.isNull(webTools)) {
            webTools = new LinkedHashMap<>();
            config.setWebTools(webTools);
        }

        // 预定义网页工具
        addWebToolIfNotExists("正则表达式", "https://c.runoob.com/front-end/854");
        addWebToolIfNotExists("进制转换器", "https://c.runoob.com/front-end/58");
        addWebToolIfNotExists("图片转BASE64", "https://c.runoob.com/front-end/59");
        addWebToolIfNotExists("Unicode编码转换", "https://c.runoob.com/front-end/3602");
        addWebToolIfNotExists("汉字转拼音", "https://c.runoob.com/front-end/5523");
        addWebToolIfNotExists("繁体字转换器", "https://c.runoob.com/front-end/5579");
        addWebToolIfNotExists("MAVEN包搜索", "https://search.maven.org");
        addWebToolIfNotExists("文件对比", "https://tool.oschina.net/diff");

        // 加载到菜单栏
        Menu menu = new Menu("网页工具");
        toolMenu.getItems().addAll(0, List.of(menu, new SeparatorMenuItem()));
        webTools.forEach((k, v) -> {
            EventHandler<ActionEvent> handler = actionEvent -> {
                WebView browser = getWebView(k, v);
                FinalUtils.openTab(browser, "网页工具-" + k);
            };
            menu.getItems().add(FxUtils.createBarMenuItem(k, handler));
            registerAction("网页工具-webtool/" + k, handler);
        });
    }

    private void addWebToolIfNotExists(String name, String url) {
        Map<String, String> map = config.getWebTools();
        if (map.containsKey(name)) {
            return;
        }
        map.put(name, url);
    }

    private WebView getWebView(String name, String url) {
        WebView browser = WEB_TOOL_BROWSER_MAP.get(name);
        if (Objects.isNull(browser)) {
            browser = newWebView();
            WEB_TOOL_BROWSER_MAP.put(name, browser);
        }

        WebEngine webEngine = browser.getEngine();
        webEngine.getHistory().getEntries().clear();
        if (url.startsWith("file:")) {
            webEngine.loadContent(FileUtil.readUtf8String(url.substring(5)));
        } else if (url.startsWith("http:") || url.startsWith("https:")) {
            webEngine.load(url);
        } else {
            throw ToDialogException.ofError("格式配置错误，格式请参考：file:c:\\Users\\tool.html, file:/root/tool.html, http://localhost:8080/tool.html");
        }

        return browser;
    }

    private void runHutoolCmd(ActionEvent actionEvent) {
        String hutoolPath = StrUtil.removeSuffix(System.getenv("HUTOOL_PATH"), File.separator);
        if (!FileUtil.exist(hutoolPath)) {
            FxDialogs.showError("请先安装HutoolCli工具");
            FxUtils.openLink("https://gitee.com/code4everything/hutool-cli");
            return;
        }

        String cmd = StrUtil.removePrefix(actionEvent.getSource().toString(), "hutool").trim();
        String jarCmd = StrUtil.format("java -jar {}{}hutool.jar {}", hutoolPath, File.separator, cmd);
        String result = RuntimeUtil.execForStr(jarCmd);
        FxDialogs.showTextAreaDialog(StrUtil.format("{} 命令执行结果", cmd), result);
    }

    private void closeSelectedTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(tab)) {
            tabPane.getTabs().remove(tab);
        }
    }

    private void registerShortcuts() {
        // ctrl+p 聚焦到工具搜索
        List<Integer> shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_P);
        FxUtils.registerShortcuts(shortcuts, () -> toolSearchBox.requestFocus());

        // escape 取消控件聚焦
        shortcuts = List.of(NativeKeyEvent.VC_ESCAPE);
        FxUtils.registerShortcuts(shortcuts, () -> {
            if (WeApplication.isRootPane()) {
                if (hiddenControl.isFocused()) {
                    hiddenControl.setText(StrUtil.EMPTY);
                    FxUtils.hideStage();
                } else {
                    hiddenControl.requestFocus();
                }
            } else {
                WeApplication.recoverRootPane();
            }
        });

        // ctrl+f4 关闭选中的选项卡
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_F4);
        FxUtils.registerShortcuts(shortcuts, this::closeSelectedTab);

        // ctrl+shift+f4 关闭选中的选项卡
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_F4);
        FxUtils.registerShortcuts(shortcuts, () -> {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().clear();
            if (Objects.nonNull(tab)) {
                tabPane.getTabs().add(tab);
            }
        });

        // ctrl+o 打开文件
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_O);
        FxUtils.registerShortcuts(shortcuts, this::openFile);

        // ctrl+shift+o 打开多个文件
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_O);
        FxUtils.registerShortcuts(shortcuts, this::openMultiFile);

        // ctrl+d 打开文件夹
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_D);
        FxUtils.registerShortcuts(shortcuts, this::openFolder);

        // ctrl+s 保存文件
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_S);
        FxUtils.registerShortcuts(shortcuts, this::saveFile);

        // ctrl+alt+shift+r 重启
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_R);
        FxUtils.registerShortcuts(shortcuts, this::restart);

        // ctrl+shift+p 打开插件面板
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_P);
        FxUtils.registerShortcuts(shortcuts, this::pluginPane);

        // ctrl+1...9 打开指定选项卡
        shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_9);
        FxUtils.registerShortcuts(shortcuts, () -> tabPane.getSelectionModel().selectLast());
        for (int i = 1; i < 9; i++) {
            int idx = i - 1;
            shortcuts = List.of(NativeKeyEvent.VC_CONTROL, i + 1);
            FxUtils.registerShortcuts(shortcuts, () -> {
                if (tabPane.getTabs().isEmpty()) {
                    return;
                }
                int maxIdx = tabPane.getTabs().size() - 1;
                tabPane.getSelectionModel().select(Math.min(maxIdx, idx));
            });
        }

        // alt+1...9 关闭指定选项卡
        shortcuts = List.of(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_9);
        FxUtils.registerShortcuts(shortcuts, () -> closeTab(tabPane.getTabs().size() - 1));
        for (int i = 1; i < 9; i++) {
            int idx = i - 1;
            shortcuts = List.of(NativeKeyEvent.VC_ALT, i + 1);
            FxUtils.registerShortcuts(shortcuts, () -> closeTab(idx));
        }
    }

    private void registerGlobalShortcuts() {
        // 全局快捷键
        List<Integer> shortcuts = List.of(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_ENTER);
        FxUtils.registerGlobalShortcuts(shortcuts, () -> {
            if (FxUtils.getStage().isShowing()) {
                FxUtils.hideStage();
            } else {
                Platform.runLater(() -> {
                    FxUtils.getStage().show();
                    toolSearchBox.requestFocus();
                });
            }
        });
    }

    private void closeTab(int idx) {
        int maxIdx = tabPane.getTabs().size() - 1;
        if (maxIdx < 0) {
            return;
        }
        idx = Math.min(idx, maxIdx);
        tabPane.getTabs().remove(idx);
    }

    private void multiDesktopOnWindows() {
        if (Objects.isNull(config.getWinVirtualDesktopHotCorner()) || !SystemUtil.getOsInfo().isWindows()) {
            return;
        }

        EventCenter.subscribeEvent(EventCenter.EVENT_MOUSE_CORNER_TRIGGER, new BaseMouseCornerEventHandler() {
            @Override
            public void handleEvent0(String s, Date date, MouseCornerEventMessage message) {
                if (config.getWinVirtualDesktopHotCorner() == message.getType()) {
                    FxUtils.multiDesktopOnWindows();
                }
            }
        });
    }

    public void loadPluginsHandy() {
        FxUtils.chooseFiles(files -> PluginLoader.loadPlugins(files, false));
    }

    private void loadToolMenus(Menu menu) {
        menu.getItems().forEach(item -> {
            if (item instanceof Menu) {
                loadToolMenus((Menu) item);
            } else if (StrUtil.isNotEmpty(item.getId())) {
                item.setOnAction(e -> openTab(TAB_MAP.get(item.getId())));
            }
        });
    }

    private void setQuickStartMenu(Menu menu, Set<WeStart> starts) {
        starts.forEach(start -> {
            if (CollUtil.isEmpty(start.getSubStarts())) {
                // 添加子菜单
                MenuItem item = new MenuItem(start.getAlias());
                item.setOnAction(e -> {
                    QuickStartEventMessage message = QuickStartEventMessage.of(start.getLocation());
                    EventCenter.publishEvent(EventCenter.EVENT_QUICK_START_CLICKED, DateUtil.date(), message);
                    FxUtils.openFile(start.getLocation());
                });
                menu.getItems().add(item);
            } else {
                // 添加父级菜单
                Menu subMenu = new Menu(start.getAlias());
                menu.getItems().add(subMenu);
                setQuickStartMenu(subMenu, start.getSubStarts());
            }
        });
    }

    private void watchClipboard(Date date) {
        String clipboard;
        String last;
        try {
            // 忽略系统休眠时抛出的异常
            clipboard = ClipboardUtil.getStr();
            last = config.getLastClipboardHistoryItem().getValue();
        } catch (Exception e) {
            WeUtils.printDebug(e.getMessage());
            clipboard = last = "";
        }
        if (StrUtil.isEmpty(clipboard) || last.equals(clipboard)) {
            return;
        }

        // 剪贴板发生变化
        String compress = WeUtils.compressString(clipboard);
        log.info("clipboard changed: {}", compress);
        config.appendClipboardHistory(date, clipboard);
        EventCenter.publishEvent(EventCenter.EVENT_CLIPBOARD_CHANGED, date, ClipboardChangedEventMessage.of(clipboard));
    }

    private void loadTabs() {
        for (String tabName : config.getInitialize().getTabs().getLoads()) {
            openTab(TAB_MAP.get(tabName));
        }
    }

    private void openTab(Pair<String, String> tabPair) {
        if (Objects.isNull(tabPair)) {
            return;
        }
        Pane box = FxUtils.loadFxml(WeApplication.class, tabPair.getValue(), true);
        if (Objects.isNull(box)) {
            return;
        }
        // 打开选项卡
        FinalUtils.openTab(box, tabPair.getKey());
    }

    public void openFile() {
        FxUtils.chooseFile(file -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openFile(file);
            }
        });
    }

    public void saveFile() {
        FxUtils.saveFile(file -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.saveFile(file);
            }
        });
    }

    public void openMultiFile() {
        FxUtils.chooseFiles(files -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openMultiFiles(files);
            }
        });
    }

    public void openFolder() {
        FxUtils.chooseFolder(folder -> {
            BaseViewController controller = FxUtils.getSelectedTabController();
            if (ObjectUtil.isNotNull(controller)) {
                controller.openFolder(folder);
            }
        });
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
        TAB_MAP.forEach((k, v) -> openTab(v));
    }

    public void openLog() {
        FxUtils.openFile(FileConsts.LOG);
    }

    public void restart() {
        FxUtils.restart();
    }

    public void openConfig() {
        FinalUtils.openConfig();
    }

    public void pluginPane() {
        Pane pane = FxUtils.loadFxml("/views/PluginManagerView.fxml", false);
        FxDialogs.showDialog("插件面板", pane, null);
    }

    public void openPluginFolder() {
        FinalUtils.openPluginFolder();
    }

    public void openLogFolder() {
        FxUtils.openFile(FileConsts.LOG_FOLDER);
    }

    public void openWorkFolder() {
        FxUtils.openFile(FileUtils.currentWorkDir());
    }

    public void seeJavaInfo() {
        TextArea area = new TextArea(getAllJavaInfos());
        VBox.setVgrow(area, Priority.ALWAYS);
        VBox box = new VBox(area);
        box.setPrefWidth(600);
        box.setPrefHeight(700);
        FxDialogs.showDialog(null, box);
    }

    public void clearAllFxmlCache() {
        EventCenter.publishEvent(EventCenter.EVENT_CLEAR_FXML_CACHE, DateUtil.date());
        tabPane.getTabs().clear();
        WEB_TOOL_BROWSER_MAP.clear();
        BeanFactory.clearCache();
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private String getAllJavaInfos() {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaVirtualMachineSpecification信息：\r\n");
        String lineSep = "========================================================================================\r\n";
        builder.append(lineSep);
        builder.append(SystemUtil.getJvmSpecInfo());

        builder.append("\r\nJavaVirtualMachineImplementation信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getJvmInfo());

        builder.append("\r\nJavaSpecification信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getJavaSpecInfo());

        builder.append("\r\nJavaImplementation信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getJavaInfo());

        builder.append("\r\nJava运行时信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getJavaRuntimeInfo());

        builder.append("\r\n系统信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getOsInfo());

        builder.append("\r\n用户信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getUserInfo());

        builder.append("\r\n当前主机网络地址信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getHostInfo());

        builder.append("\r\n运行时信息：\r\n");
        builder.append(lineSep);
        builder.append(SystemUtil.getRuntimeInfo());

        return builder.toString();
    }

    public void toolBoxKeyReleased(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (!keyCode.isWhitespaceKey() && keyCode != KeyCode.ENTER) {
            return;
        }

        endCaretPosition();
        String keyword = toolSearchBox.getValue();
        if (StrUtil.isBlank(keyword)) {
            return;
        }

        if (keyCode == KeyCode.ENTER) {
            String firstName = CollUtil.getFirst(toolSearchBox.getItems());
            toolSearchBox.getItems().clear();
            toolSearchBox.setValue(StrUtil.EMPTY);
            EventHandler<ActionEvent> eventHandler = getActionEventEventHandler(keyword, firstName);

            if (Objects.isNull(eventHandler)) {
                FxDialogs.showError("未找到对应的工具！");
            } else {
                eventHandler.handle(new ActionEvent(keyword, null));
            }

            return;
        }

        toolSearchBox.getItems().clear();
        String[] tokenizer = StrUtil.splitTrim(keyword, " ").toArray(new String[0]);
        ACTION_MAP.forEach((k, v) -> {
            if (k.endsWith(StringConsts.Sign.STAR)) {
                // 模式匹配，不适用下拉框提示
                return;
            }
            String pinyin = ACTION_NAME_PINYIN_MAP.get(k);
            if (containsAllIgnoreCase(pinyin, tokenizer) || containsAllIgnoreCase(k, tokenizer)) {
                toolSearchBox.getItems().add(k);
            }
        });

        if (CollUtil.isNotEmpty(toolSearchBox.getItems())) {
            toolSearchBox.show();
        }
    }

    private EventHandler<ActionEvent> getActionEventEventHandler(String keyword, String firstName) {
        keyword = StrUtil.nullToEmpty(keyword);
        String trimmedKeyword = keyword.trim();
        EventHandler<ActionEvent> eventHandler = ACTION_MAP.get(trimmedKeyword);
        if (Objects.nonNull(eventHandler)) {
            return eventHandler;
        }

        // 模式匹配
        eventHandler = actionCache.get(trimmedKeyword, () -> {
            for (Map.Entry<String, EventHandler<ActionEvent>> entry : ACTION_MAP.entrySet()) {
                String key = entry.getKey();
                if (!key.endsWith(StringConsts.Sign.STAR) || key.equals(StringConsts.Sign.STAR)) {
                    continue;
                }

                key = key.substring(0, key.length() - 1);
                if (trimmedKeyword.startsWith(key) && Objects.nonNull(entry.getValue())) {
                    return entry.getValue();
                }
            }
            return null;
        });

        if (Objects.nonNull(eventHandler)) {
            return eventHandler;
        }
        return StrUtil.isEmpty(firstName) || HUTOOL_CMD_PATTERN.matcher(keyword).matches() ? hutoolCmdHandler : ACTION_MAP.get(firstName);
    }

    public boolean containsAllIgnoreCase(String str, String[] keys) {
        if (StrUtil.isEmpty(str) || ArrayUtil.isEmpty(keys)) {
            return false;
        }
        for (String key : keys) {
            if (!StrUtil.containsIgnoreCase(str, key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将光标移到尾部
     */
    private void endCaretPosition() {
        // 通过失去焦点，获取焦点，片刻获取变化后的值
        tabPane.requestFocus();
        toolSearchBox.requestFocus();
        toolSearchBox.getEditor().positionCaret(Integer.MAX_VALUE);
    }

    public void locateControl(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.X) {
            closeSelectedTab();
            hiddenControl.setText(StrUtil.EMPTY);
            return;
        }

        FxUtils.enterDo(keyEvent, () -> {
            int idx = 0;
            String text = hiddenControl.getText();
            hiddenControl.setText(StrUtil.EMPTY);
            try {
                idx = NumberUtil.parseInt(text);
            } catch (Exception e) {
                // ignore
            }

            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (Objects.isNull(tab)) {
                return;
            }

            List<Control> list = new ArrayList<>();
            parseTabControl(list, tab.getContent());

            if (CollUtil.isEmpty(list)) {
                return;
            }
            idx = Math.max(0, idx - 1);
            list.get(Math.min(list.size() - 1, idx)).requestFocus();
        });
    }

    private void parseTabControl(List<Control> list, Node node) {
        if (node instanceof Pane) {
            Pane pane = (Pane) node;
            pane.getChildren().forEach(e -> parseTabControl(list, e));
        } else if (node instanceof ScrollPane) {
            ScrollPane pane = (ScrollPane) node;
            parseTabControl(list, pane.getContent());
        } else if (node instanceof SplitPane) {
            SplitPane pane = (SplitPane) node;
            pane.getItems().forEach(e -> parseTabControl(list, e));
        } else if (node instanceof TitledPane) {
            TitledPane pane = (TitledPane) node;
            parseTabControl(list, pane.getContent());
        } else if (node instanceof TabPane) {
            TabPane pane = (TabPane) node;
            Tab tab = pane.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(tab)) {
                parseTabControl(list, tab.getContent());
            }
        } else if (node instanceof Control) {
            Control control = (Control) node;
            if (control.isVisible() && !control.isDisable()) {
                list.add(control);
            }
        }
    }

    public void checkUpdate() {
        new UpdateService().checkUpdate();
    }

    public void httpFileBrowser() {
        HttpFileBrowserService.getInstance().httpFileBrowserDialog();
    }
}
