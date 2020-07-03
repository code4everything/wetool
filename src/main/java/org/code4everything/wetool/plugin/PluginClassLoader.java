package org.code4everything.wetool.plugin;

import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author pantao
 * @since 2020/1/14
 */
public class PluginClassLoader extends JarClassLoader {

    @Getter
    private String name;

    public PluginClassLoader(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return StrUtil.format("{} plugin class loader", name);
    }
}
