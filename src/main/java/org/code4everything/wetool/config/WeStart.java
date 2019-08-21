package org.code4everything.wetool.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.code4everything.boot.base.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author pantao
 * @since 2019/8/21
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeStart implements BaseBean, Serializable {

    private static final long serialVersionUID = 762565001230119596L;

    /**
     * 菜单名称
     */
    private String alias;

    /**
     * 不包含子菜单时：快速打开文件的路径
     */
    private String location;

    /**
     * 子菜单
     */
    private List<WeStart> subStarts;

    @Override
    public void requireNonNullProperty() {
        String msg = "the value of field '%s' at class '" + this.getClass().getName() + "' must not be empty";
        if (StrUtil.isEmpty(alias)) {
            throw new NullPointerException(String.format(msg, "alias"));
        }
        if (CollUtil.isNotEmpty(subStarts)) {
            subStarts.forEach(WeStart::requireNonNullProperty);
        } else if (StrUtil.isEmpty(location)) {
            throw new NullPointerException(String.format(msg, "location"));
        }
    }
}
