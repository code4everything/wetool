package org.code4everything.wetool.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TitleConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.handler.BaseClipboardChangedEventHandler;
import org.code4everything.wetool.plugin.support.event.message.ClipboardChangedEventMessage;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;

import java.util.Date;

/**
 * @author pantao
 * @since 2018/4/3
 */
@Slf4j
public class ClipboardHistoryController implements BaseViewController {

    private static final String SEP = StrUtil.repeat("=", 100);

    private static final String TEMPLATE = SEP + "\r\n{}\r\n" + SEP + "\r\n\r\n{}\r\n\r\n";

    @FXML
    public TextArea clipboardHistory;

    private WeConfig config = WeUtils.getConfig();

    @FXML
    private void initialize() {
        log.info("open tab for clipboard history");
        clipboardHistory.setWrapText(config.getAutoWrap());

        FinalUtils.registerView(TitleConsts.CLIPBOARD_HISTORY, this);
        for (Pair<Date, String> pair : config.getClipboardHistory()) {
            insert(pair.getKey(), pair.getValue());
        }

        EventCenter.subscribeEvent(EventCenter.EVENT_CLIPBOARD_CHANGED, new BaseClipboardChangedEventHandler() {
            @Override
            public void handleEvent0(String s, Date date, ClipboardChangedEventMessage message) {
                Platform.runLater(() -> insert(date, message.getClipboardText()));
            }
        });
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
