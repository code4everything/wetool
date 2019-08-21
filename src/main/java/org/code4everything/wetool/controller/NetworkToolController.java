package org.code4everything.wetool.controller;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;

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

    @FXML
    private void initialize() {
        log.info("open tab for network tool");
        BeanFactory.registerView(TitleConsts.NETWORK_TOOL, this);
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
}
