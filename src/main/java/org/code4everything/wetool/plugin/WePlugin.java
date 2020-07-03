package org.code4everything.wetool.plugin;

import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ObjectUtil;
import lombok.*;
import org.code4everything.boot.base.bean.BaseBean;
import org.code4everything.wetool.plugin.support.config.WePluginInfo;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author pantao
 * @since 2019/9/26
 */
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class WePlugin implements BaseBean, Serializable {

    private static final long serialVersionUID = 8644286685582338724L;

    private static final PluginClassLoader CLASS_LOADER = new PluginClassLoader("default");

    @NonNull
    private WePluginInfo pluginInfo;

    @NonNull
    private File jarFile;

    private PluginClassLoader classLoader;

    @Generated
    public JarClassLoader getClassLoader() {
        return ObjectUtil.defaultIfNull(classLoader, CLASS_LOADER);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        return equals(pluginInfo, ((WePlugin) that).getPluginInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginInfo.getAuthor(), pluginInfo.getName());
    }

    private boolean equals(WePluginInfo thisInfo, WePluginInfo thatInfo) {
        return thisInfo.getAuthor().equals(thatInfo.getAuthor()) && thisInfo.getName().equals(thatInfo.getName());
    }
}
