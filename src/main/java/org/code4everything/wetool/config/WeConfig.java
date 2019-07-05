package org.code4everything.wetool.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.swing.ClipboardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.*;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author pantao
 * @since 2019/7/3
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeConfig implements BaseBean, Serializable {

    private static final long serialVersionUID = 6105929832284264685L;

    private WeInitialize initialize;

    private Integer clipboardSize;

    private Boolean autoWrap;

    private String fileFilter;

    private String fileChooserInitDir;

    private transient LinkedList<Pair<Date, String>> clipboardHistory = new LinkedList<>();

    private transient Pattern filterPattern;

    @Generated
    public Pattern getFilterPattern() {
        if (Objects.isNull(filterPattern)) {
            filterPattern = Pattern.compile(fileFilter);
        }
        return filterPattern;
    }

    /**
     * 禁止外部设置
     */
    @Generated
    private void setFilterPattern(Pattern filterPattern) {
        this.filterPattern = filterPattern;
    }


    @Generated
    public String getFileChooserInitDir() {
        return StrUtil.isEmpty(fileChooserInitDir) ? FileUtil.getUserHomePath() : fileChooserInitDir;
    }

    public void appendClipboardHistory(Date date, String content) {
        if (clipboardHistory.size() < clipboardSize) {
            clipboardHistory.add(new Pair<>(date, StrUtil.nullToEmpty(content)));
        } else {
            clipboardHistory.removeFirst();
            appendClipboardHistory(date, content);
        }
    }

    public Pair<Date, String> getLastClipboardHistoryItem() {
        Pair<Date, String> last = clipboardHistory.getLast();
        return ObjectUtil.defaultIfNull(last, new Pair<>(new Date(), StrUtil.nullToEmpty(ClipboardUtil.getStr())));
    }
}
