/**
 * @author pantao
 * @since 2020/1/9
 */
module org.code4everything.wetool {
    requires java.base;

    requires org.mapstruct;

    requires hutool.core;
    requires hutool.system;
    requires hutool.crypto;
    requires hutool.extra;

    requires boot.surface;
    requires fastjson;
    requires com.google.common;

    requires org.code4everything.wetool.plugin.support;

    exports org.code4everything.wetool;

    opens views;
    opens images;
    opens org.code4everything.wetool.controller;
    opens org.code4everything.wetool.controller.converter;
    opens org.code4everything.wetool.controller.generator;
    opens org.code4everything.wetool.controller.parser;
}