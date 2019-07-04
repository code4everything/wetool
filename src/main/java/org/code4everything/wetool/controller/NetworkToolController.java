package org.code4everything.wetool.controller;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.zhazhapan.util.dialog.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.FxUtils;

/**
 * @author pantao
 * @since 2018/4/13
 */
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
        BeanFactory.registerView(TitleConsts.NETWORK_TOOL, this);
        Platform.runLater(() -> {
            try {
                privateIpv4.setText(NetUtil.getLocalhostStr());
                macAddress.setText(NetUtil.getLocalMacAddress());
                OsInfo info = SystemUtil.getOsInfo();
                systemInfo.setText(info.getName() + " " + info.getArch());
            } catch (Exception e) {
                Alerts.showError(TitleConsts.APP_TITLE, TipConsts.NETWORK_ERROR);
            }
        });
    }

    public void keyReleased(KeyEvent keyEvent) {
        FxUtils.enterDo(keyEvent, this::parseDomain);
    }

    public void parseDomain() {
        if (StrUtil.isEmpty(domain.getText())) {
            return;
        }
        domainIp.setText(NetUtil.getIpByHost(domain.getText()));
    }
}
