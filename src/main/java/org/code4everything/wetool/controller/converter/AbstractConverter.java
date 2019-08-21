package org.code4everything.wetool.controller.converter;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.controller.BaseViewController;
import org.code4everything.wetool.factory.BeanFactory;

/**
 * @author pantao
 * @since 2019/7/8
 **/
public abstract class AbstractConverter implements BaseViewController {

    protected final WeConfig config = BeanFactory.get(WeConfig.class);

    void initConverter(TextArea srcArea, TextArea destArea, ComboBox<String> srcNary, ComboBox<String> destNary) {
        // 监听下拉框事件
        srcNary.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());
        destNary.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> convert());

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
