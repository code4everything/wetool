package org.code4everything.wetool.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.swing.ClipboardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.*;
import org.code4everything.boot.base.FileUtils;
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

    public static final String PATH = FileUtils.currentWorkDir("we-config.json");

    private static final long serialVersionUID = 6105929832284264685L;

    private WeInitialize initialize;

    /**
     * 剪贴板列表长度
     */
    private Integer clipboardSize;

    /**
     * 文本框是否自动换行
     */
    private Boolean autoWrap;

    /**
     * 文件过滤
     */
    private String fileFilter;

    /**
     * 初始化选择文件的路径
     */
    private String fileChooserInitDir;

    /**
     * 字符串记录到日志的长度（会压缩裁剪）
     */
    private Integer logCompressLen;

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
