package com.zhazhapan.util.visual.controller;

import cn.hutool.core.util.NetUtil;
import com.zhazhapan.config.JsonParser;
import com.zhazhapan.util.NetUtils;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

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
    public TextField computerName;

    @FXML
    public TextField systemInfo;

    @FXML
    public TitledPane firstTitledPane;

    @FXML
    public Accordion accordion;

    @FXML
    private void initialize() {
        accordion.setExpandedPane(firstTitledPane);
        ControllerModel.setNetworkToolController(this);
        try {
            privateIpv4.setText(NetUtils.getLocalIp());
            JsonParser parser = NetUtils.getPublicIpAndLocation();
            publicIp.setText(parser.getStringUseEval("ip"));
            publicAddress.setText(parser.getStringUseEval("address"));
            macAddress.setText(NetUtil.getLocalMacAddress());
            computerName.setText(NetUtils.getComputerName());
            systemInfo.setText(NetUtils.getSystemName() + " " + NetUtils.getSystemArch() + " " + NetUtils
                    .getSystemVersion());
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.NETWORK_TOOL);
        }
    }
}
