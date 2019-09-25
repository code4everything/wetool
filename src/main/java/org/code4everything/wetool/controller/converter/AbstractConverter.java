package org.code4everything.wetool.controller.converter;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.util.WeUtils;

/**
 * @author pantao
 * @since 2019/7/8
 **/
public abstract class AbstractConverter implements BaseViewController {

    protected final WeConfig config = WeUtils.getConfig();

    void initConverter(TextArea srcArea, TextArea destArea, ComboBox<String> srcCB, ComboBox<String> destCB) {
        // 监听下拉框事件
        srcCB.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        destCB.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());

        // 设置文本框
        srcArea.textProperty().addListener((o, ov, nv) -> convert());
        srcArea.setWrapText(config.getAutoWrap());
        destArea.setWrapText(config.getAutoWrap());
    }

    /**
     * 转换器
     */
    abstract void convert();
}
