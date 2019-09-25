package org.code4everything.wetool.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.code4everything.wetool.plugin.support.util.WeUtils;

import java.util.Date;

/**
 * @author pantao
 * @since 2018/4/3
 */
@Slf4j
public class ClipboardHistoryController implements BaseViewController {

    private final String SEP = StrUtil.repeat("=", 100);

    private final String TEMPLATE = SEP + "\r\n{}\r\n" + SEP + "\r\n\r\n{}\r\n\r\n";

    @FXML
    public TextArea clipboardHistory;

    private WeConfig config = WeUtils.getConfig();

    @FXML
    private void initialize() {
        log.info("open tab for clipboard history");
        BeanFactory.registerView(TitleConsts.CLIPBOARD_HISTORY, this);
        clipboardHistory.setWrapText(config.getAutoWrap());
        for (Pair<Date, String> pair : config.getClipboardHistory()) {
            insert(pair.getKey(), pair.getValue());
        }
    }

    void insert(Date date, String content) {
        if (StrUtil.isNotEmpty(content)) {
            content = StrUtil.format(TEMPLATE, DateUtil.formatDateTime(date), content);
            clipboardHistory.insertText(0, content);
        }
    }

    @Override
    public String getSavingContent() {
        return clipboardHistory.getText();
    }
}
