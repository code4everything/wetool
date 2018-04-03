package com.zhazhapan.util.visual.controller;

import cn.hutool.core.util.ClipboardUtil;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.visual.model.ControllerModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

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
        ControllerModel.setClipboardHistoryController(this);
        String dateVariable = "%datetime%";
        String contentVariable = "%content%";
        String template = "---------------------------------------\r\n" + dateVariable + "\r\n" +
                "---------------------------------------\r\n\r\n" + contentVariable + "\r\n\r\n";
        StringBuilder builder = new StringBuilder(template.replace(dateVariable, Formatter.datetimeToString(new Date
                ()).replace(contentVariable, ClipboardUtil.getStr())));
        clipboardHistory.setText(builder.toString());
    }
}
