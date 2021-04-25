package org.code4everything.wetool.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Holder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import org.code4everything.wetool.handler.HttpFileBrowserHandler;
import org.code4everything.wetool.plugin.support.http.HttpService;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author pantao
 * @since 2021/4/25
 */
public class HttpFileBrowserService implements EventHandler<ActionEvent> {

    private static volatile HttpFileBrowserService httpFileBrowserService;

    private final Set<HttpFileBrowserHandler> handlerSet = new HashSet<>();

    private HttpFileBrowserService() {}

    public static HttpFileBrowserService getInstance() {
        if (Objects.isNull(httpFileBrowserService)) {
            synchronized (HttpFileBrowserService.class) {
                if (Objects.isNull(httpFileBrowserService)) {
                    httpFileBrowserService = new HttpFileBrowserService();
                }
            }
        }
        return httpFileBrowserService;
    }

    public void browse(int port, String apiPattern) {
        FxUtils.chooseFolder(file -> browse(port, apiPattern, file.getAbsolutePath()));
    }

    public synchronized void browse(int port, String apiPattern, String rootPath) {
        HttpFileBrowserHandler handler = new HttpFileBrowserHandler(port, apiPattern, rootPath);
        if (handlerSet.contains(handler)) {
            FxDialogs.showError("接口已存在：" + apiPattern);
            return;
        }

        handler.export();
        handlerSet.add(handler);
    }

    @Override
    public void handle(ActionEvent event) {
        String cmd = StrUtil.removePrefix(event.getSource().toString(), "file-browser");
        if (StrUtil.isBlank(cmd)) {
            httpFileBrowserDialog();
            return;
        }
        handleExportCmd(cmd);
    }

    public void httpFileBrowserDialog() {
        TextInputDialog dialog = FxDialogs.getTextInputDialog("开启HTTP文件浏览服务", "格式：[port:]get/api/file/* [/root/path]");
        dialog.getEditor().setText("8080:get/file/* /home/test");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isEmpty()) {
            return;
        }
        httpFileBrowserService.handleExportCmd(optional.get());
    }

    private void handleExportCmd(String cmd) {
        String[] segment = StrUtil.split(StrUtil.trim(cmd), " ");
        if (ArrayUtil.isEmpty(segment)) {
            FxDialogs.showError("命令格式错误！");
            return;
        }

        String[] api = StrUtil.split(segment[0], ":");
        if (ArrayUtil.isEmpty(segment)) {
            FxDialogs.showError("命令格式错误！");
            return;
        }

        int port = HttpService.getDefaultPort();
        String apiPattern;
        if (api.length > 1) {
            port = NumberUtil.parseInt(api[0]);
            apiPattern = api[1];
        } else {
            apiPattern = api[0];
        }

        Holder<String> rootPath = new Holder<>();
        if (segment.length > 1) {
            rootPath.set(segment[1]);
        } else {
            FxUtils.chooseFolder(file -> rootPath.set(file.getAbsolutePath()));
        }

        if (!FileUtil.exist(rootPath.get())) {
            FxDialogs.showError("根目录未设置");
            return;
        }

        browse(port, apiPattern, rootPath.get());
        FxDialogs.showSuccess();
    }
}
