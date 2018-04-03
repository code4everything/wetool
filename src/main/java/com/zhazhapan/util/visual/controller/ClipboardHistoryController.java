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

    private final String dateVariable = "%datetime%";
    private final String contentVariable = "%content%";
    private final String template = "---------------------------------------\r\n" + dateVariable + "\r\n" +
            "---------------------------------------\r\n\r\n" + contentVariable + "\r\n\r\n";
    @FXML
    public TextArea clipboardHistory;

    @FXML
    private void initialize() {
        ControllerModel.setClipboardHistoryController(this);

        StringBuilder builder = new StringBuilder();
        for (int i = ConfigModel.getClipboardHistorySize() - 1; i >= 0; i--) {
            Pair<Date, String> pair = ConfigModel.getClipboardHistoryItem(i);
            if (Checker.isNotNull(pair)) {
                builder.append(template.replace(dateVariable, DateUtil.formatDateTime(pair.getKey())).replace
                        (contentVariable, pair.getValue()));
            }
        }
        clipboardHistory.setText(builder.toString());
    }

    public void insert(Date date, String content) {
        content = template.replace(dateVariable, DateUtil.formatDateTime(date)).replace(contentVariable, content);
        clipboardHistory.setText(content + clipboardHistory.getText());
    }
}
