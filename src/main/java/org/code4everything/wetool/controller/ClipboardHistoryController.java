package org.code4everything.wetool.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.util.Pair;
import org.code4everything.wetool.factor.BeanFactory;
import org.code4everything.wetool.model.ConfigModel;

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
        // 注册
        BeanFactory.register(this);

        clipboardHistory.setWrapText(ConfigModel.isAutoWrap());
        for (int i = ConfigModel.getClipboardHistorySize() - 1; i >= 0; i--) {
            Pair<Date, String> pair = ConfigModel.getClipboardHistoryItem(i);
            if (ObjectUtil.isNotNull(pair)) {
                insert(pair.getKey(), pair.getValue());
            }
        }
    }

    void insert(Date date, String content) {
        if (StrUtil.isNotEmpty(content)) {
            String contentVariable = "%content%";
            String dateVariable = "%datetime%";
            String template = "---------------------------------------\r\n" + dateVariable + "\r\n" +
                    "---------------------------------------\r\n\r\n" + contentVariable + "\r\n\r\n";
            content = template.replace(dateVariable, DateUtil.formatDateTime(date)).replace(contentVariable, content);
            clipboardHistory.setText(content + clipboardHistory.getText());
        }
    }
}
