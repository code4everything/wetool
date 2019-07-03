package org.code4everything.wetool.controller;

import cn.hutool.core.util.NetUtil;
import com.zhazhapan.config.JsonParser;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.NetUtils;
import com.zhazhapan.util.ThreadPool;
import com.zhazhapan.util.dialog.Alerts;
import org.code4everything.wetool.WeUtils;
import org.code4everything.wetool.constant.LocalValueConsts;
import org.code4everything.wetool.model.ControllerModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author pantao
 * @since 2018/4/13
 */
public class NetworkToolController {

    @FXML
    public TextField privateIpv4;

    @FXML
    public TextField privateIpv6;

    @FXML
    public TextField publicIp;

    @FXML
    public TextField publicAddress;

    @FXML
    public TextField macAddress;

    @FXML
    public TextField systemInfo;

    @FXML
    public TextField ipAddress;

    @FXML
    public TextField ipLocation;

    @FXML
    public TextField domain;

    @FXML
    public TextArea whoisResult;

    @FXML
    private void initialize() {
        ControllerModel.setNetworkToolController(this);
        //防止UI线程阻塞
        ThreadPool.executor.submit(() -> {
            try {
                JsonParser parser = NetUtils.getPublicIpAndLocation();
                Platform.runLater(() -> {
                    try {
                        publicIp.setText(parser.getStringUseEval("ip"));
                        publicAddress.setText(parser.getStringUseEval("address"));
                        privateIpv4.setText(NetUtils.getLocalIp());
                        macAddress.setText(NetUtil.getLocalMacAddress());
                        systemInfo.setText(NetUtils.getSystemName() + " " + NetUtils.getSystemArch() + " " + NetUtils.getSystemVersion());
                    } catch (Exception e) {
                        Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NETWORK_ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NETWORK_ERROR));
            }
        });
    }

    public void queryIpLocation() {
        String ip = ipAddress.getText();
        if (Checker.isNotEmpty(ip)) {
            ThreadPool.executor.submit(() -> {
                String location = WeUtils.getLocationByIp(ip);
                if (Checker.isNotEmpty(location)) {
                    Platform.runLater(() -> ipLocation.setText(location));
                }
            });
        }
    }

    public void queryWhois() {
        String domainName = domain.getText();
        if (Checker.isNotEmpty(domainName)) {
            String result = WeUtils.whois(domainName);
            if (Checker.isNotEmpty(result)) {
                whoisResult.setText(result);
            }
        }
    }
}
