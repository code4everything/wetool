package com.zhazhapan.util.visual.controller;

import cn.hutool.core.date.DateUtil;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

import java.util.Date;

/**
 * @author pantao
 * @since 2018/4/3
 */
public class ClipboardHistoryController {

    @FXML
    public TextArea clipboardHistory;

    @FXML
    private void initialize() {
        clipboardHistory.setWrapText(ConfigModel.isAutoWrap());
        ControllerModel.setClipboardHistoryController(this);
        for (int i = ConfigModel.getClipboardHistorySize() - 1; i >= 0; i--) {
            Pair<Date, String> pair = ConfigModel.getClipboardHistoryItem(i);
            if (Checker.isNotNull(pair)) {
                insert(pair.getKey(), pair.getValue());
            }
        }
    }

    public void insert(Date date, String content) {
        if (Checker.isNotEmpty(content)) {
            String contentVariable = "%content%";
            String dateVariable = "%datetime%";
            String template = "---------------------------------------\r\n" + dateVariable + "\r\n" +
                    "---------------------------------------\r\n\r\n" + contentVariable + "\r\n\r\n";
            content = template.replace(dateVariable, DateUtil.formatDateTime(date)).replace(contentVariable, content);
            clipboardHistory.setText(content + clipboardHistory.getText());
        }
    }
}
