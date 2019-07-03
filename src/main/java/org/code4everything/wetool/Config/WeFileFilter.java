package org.code4everything.wetool.Config;

import lombok.*;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;
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
public class WeFileFilter implements BaseBean, Serializable {

    private static final long serialVersionUID = -1000636274675406403L;

    private String regex;

    private Boolean showTip;

    private String description;

    private transient Pattern filterPattern;

    @Generated
    public Pattern getFilterPattern() {
        if (Objects.isNull(filterPattern)) {
            filterPattern = Pattern.compile(regex);
        }
        return filterPattern;
    }

    @Generated
    private void setFilterPattern(Pattern filterPattern) {
        this.filterPattern = filterPattern;
    }
}
