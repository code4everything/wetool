package org.code4everything.wetool.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.code4everything.wetool.Config.WeConfig;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.factory.BeanFactory;

import java.util.Date;

/**
 * @author pantao
 * @since 2018/4/3
 */
public class ClipboardHistoryController implements BaseViewController {

    private static final String SEP = StrUtil.repeat("=", 50);

    private static final String TEMPLATE = SEP + "\r\n{}\r\n" + SEP + "\r\n\r\n{}\r\n\r\n";

    @FXML
    public TextArea clipboardHistory;

    private WeConfig config = BeanFactory.get(WeConfig.class);

    @FXML
    private void initialize() {
        BeanFactory.registerView(TitleConsts.CLIPBOARD_HISTORY, this);
        clipboardHistory.setWrapText(config.getAutoWrap());
        for (cn.hutool.core.lang.Pair<Date, String> pair : config.getClipboardHistory()) {
            insert(pair.getKey(), pair.getValue());
        }
    }

    void insert(Date date, String content) {
        if (StrUtil.isNotEmpty(content)) {
            content = StrUtil.format(TEMPLATE, DateUtil.formatDateTime(date), content);
            clipboardHistory.setText(content + clipboardHistory.getText());
        }
    }

    @Override
    public String getSavingContent() {
        return clipboardHistory.getText();
    }
}
