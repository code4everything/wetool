package org.code4everything.wetool.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author pantao
 * @since 2018/4/13
 */
@Slf4j
public class NetworkToolController implements BaseViewController {

    @FXML
    public TextField privateIpv4;

    @FXML
    public TextField macAddress;

    @FXML
    public TextField systemInfo;

    @FXML
    public TextField domain;

    @FXML
    public TextField domainIp;

    private static final List<Integer> POST_LIST = new ArrayList<>();

    static {
        for (int i = 1; i <= 65535; i++) {
            POST_LIST.add(i);
        }
    }

    @FXML
    public TextField ipOfPortScan;

    @FXML
    public TextField availablePort;

    @FXML
    public Button scanBtn;

    @FXML
    private void initialize() {
        log.info("open tab for network tool");
        FinalUtils.registerView(TitleConsts.NETWORK_TOOL, this);
        Platform.runLater(() -> {
            privateIpv4.setText(NetUtil.getLocalhostStr());
            macAddress.setText(NetUtil.getLocalMacAddress());
            OsInfo info = SystemUtil.getOsInfo();
            systemInfo.setText(info.getName() + " " + info.getArch());
        });
    }

    public void keyReleased(KeyEvent keyEvent) {
        FxUtils.enterDo(keyEvent, this::parseDomain);
    }

    public void parseDomain() {
        if (StrUtil.isEmpty(domain.getText())) {
            return;
        }
        String ip = NetUtil.getIpByHost(domain.getText());
        log.info("parse ip for domain {}: {}", domain.getText(), ip);
        domainIp.setText(ip);
    }

    public void scanPort() {
        String ip = ipOfPortScan.getText();
        if (StrUtil.isBlank(ip)) {
            return;
        }

        scanBtn.setDisable(true);
        List<Future<Integer>> futureList = ListUtil.split(POST_LIST, 10000).stream().map(ports -> WeUtils.executeAsync(() -> {
            ports.forEach(port -> {
                try {
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(ip, port);
                    socket.connect(socketAddress, 1000);
                    socket.close();
                    Platform.runLater(() -> availablePort.setText(port + "," + availablePort.getText()));
                } catch (Exception e) {
                    // ignore
                }
            });
            return 1;
        })).collect(Collectors.toList());

        WeUtils.execute(() -> {
            futureList.forEach(e -> {
                try {
                    e.get();
                } catch (Exception x) {
                    // ignore
                }
            });

            scanBtn.setDisable(false);
            FxDialogs.showInformation("端口扫描", "扫描完毕");
        });
    }
}
