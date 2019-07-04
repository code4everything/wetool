package org.code4everything.wetool.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import com.zhazhapan.config.JsonParser;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.NetUtils;
import com.zhazhapan.util.ThreadPool;
import com.zhazhapan.util.dialog.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;
import org.code4everything.wetool.util.WeUtils;

/**
 * @author pantao
 * @since 2018/4/13
 */
public class NetworkToolController implements BaseViewController {

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
        BeanFactory.registerView(TitleConsts.NETWORK_TOOL, this);
        //防止UI线程阻塞
        ThreadUtil.execute(() -> {
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
                        Alerts.showError(TitleConsts.APP_TITLE, TipConsts.NETWORK_ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> Alerts.showError(TitleConsts.APP_TITLE, TipConsts.NETWORK_ERROR));
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

    @Override
    public String getSavingContent() {
        return whoisResult.getText();
    }
}
