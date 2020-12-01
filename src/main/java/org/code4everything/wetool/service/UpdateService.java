package org.code4everything.wetool.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.util.DialogWinnable;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author pantao
 * @since 2020/11/30
 */
public class UpdateService {

    public void checkUpdate() {
        String history = HttpUtil.get("https://gitee.com/code4everything/wetool/raw/master/history.md");
        if (StrUtil.isEmpty(history)) {
            FxDialogs.showError("网络异常");
            return;
        }

        VersionInfo versionInfo = getNewestVersionInfo(history);
        if (Objects.isNull(versionInfo) || AppConsts.CURRENT_VERSION.equals(versionInfo.getVersion())) {
            FxDialogs.showInformation(null, "当前已是最新版");
            return;
        }

        StringJoiner joiner = new StringJoiner("\n");
        versionInfo.getDescriptions().forEach(joiner::add);
        String tip = "是否更新？";
        String template = "检测到新版本：v{}，更新内容如下：\n\n{}\n\n{}\n";
        Label label = new Label(StrUtil.format(template, versionInfo.getVersion(), joiner.toString(), tip));

        VBox.setVgrow(label, Priority.NEVER);
        VBox.setMargin(label, new Insets(10, 10, 10, 10));
        VBox vBox = new VBox();
        vBox.getChildren().add(label);

        FxDialogs.showDialog("版本更新", vBox, new DialogWinnable<String>() {
            @Override
            public String convertResult() {
                return "ok";
            }

            @Override
            public void consumeResult(String result) {
                if (StrUtil.isNotBlank(result)) {
                    update(versionInfo.getDownloadUrl());
                }
            }
        });
    }

    public void update(String downloadUrl) {
        File downloadHome = FileUtil.file(FileUtil.getUserHomePath(), "Downloads");
        File file = HttpUtil.downloadFileFromUrl(downloadUrl, downloadHome);

        File wetool = FileUtil.file(downloadHome.getAbsolutePath(), "wetool");
        ZipUtil.unzip(file, wetool);

        File currWorkDir = FileUtil.file(FileUtils.currentWorkDir());
        OsInfo osInfo = SystemUtil.getOsInfo();
        String jarName = null;

        if (osInfo.isWindows()) {
            jarName = "wetool-win.jar";
            String newJar = FileUtil.file(wetool.getAbsolutePath(), jarName).getAbsolutePath();
            RuntimeUtil.exec("./update-wetool.bat", newJar, FxUtils.getWetoolJarName());
            WeUtils.exitSystem();
        } else if (osInfo.isMac()) {
            jarName = "wetool-mac.jar";
            FileUtil.move(FileUtil.file(wetool.getAbsolutePath(), jarName), currWorkDir, true);
        } else if (osInfo.isLinux()) {
            jarName = "wetool-linux.jar";
            FileUtil.move(FileUtil.file(wetool.getAbsolutePath(), jarName), currWorkDir, true);
        }

        FileUtil.del(wetool);

        if (StrUtil.isEmpty(jarName)) {
            FxDialogs.showError("更新失败：未知操作系统");
        } else {
            FxUtils.restart(jarName);
        }
    }

    public VersionInfo getNewestVersionInfo(String history) {
        List<String> blocks = StrUtil.splitTrim(history, "####");
        if (blocks.size() < 2) {
            return null;
        }

        List<String> lines = StrUtil.splitTrim(blocks.get(1), "- ");
        if (CollUtil.isEmpty(lines)) {
            return null;
        }

        String version = StrUtil.strip(lines.get(0), "[", ")");
        List<String> versionAndUrl = StrUtil.splitTrim(version, "](");
        if (versionAndUrl.size() < 2) {
            return null;
        }

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion(StrUtil.removePrefixIgnoreCase(versionAndUrl.get(0), "v"));
        versionInfo.setDownloadUrl(versionAndUrl.get(1));
        return versionInfo.setDescriptions(lines.stream().skip(1).collect(Collectors.toList()));
    }

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class VersionInfo {

        private String version;

        private String downloadUrl;

        private List<String> descriptions;
    }
}
