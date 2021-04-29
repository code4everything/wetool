/**
 * @author pantao
 * @since 2020/1/9
 */
module org.code4everything.wetool {
    requires java.base;

    requires hutool.core;
    requires hutool.system;
    requires hutool.crypto;
    requires hutool.http;
    requires hutool.cache;

    requires boot.surface;
    requires fastjson;
    requires com.google.common;
    requires jnativehook;
    requires hutool.extra;
    requires io.netty.codec.http;
    requires FXTrayIcon;
    requires com.google.zxing;

    requires transitive org.code4everything.wetool.plugin.support;

    exports org.code4everything.wetool;
    exports org.code4everything.wetool.plugin;

    opens views;
    opens images;
    opens org.code4everything.wetool.controller;
    opens org.code4everything.wetool.controller.converter;
    opens org.code4everything.wetool.controller.generator;
    opens org.code4everything.wetool.controller.parser;
}
