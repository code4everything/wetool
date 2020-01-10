/**
 * @author pantao
 * @since 2020/1/9
 */
module org.code4everything.wetool {
    requires java.base;

    requires hutool.core;
    requires hutool.system;
    requires hutool.crypto;
    requires hutool.extra;

    requires boot.surface;
    requires fastjson;
    requires com.google.common;

    requires transitive org.code4everything.wetool.plugin.support;

    exports org.code4everything.wetool;
    exports org.code4everything.wetool.plugin;
}
