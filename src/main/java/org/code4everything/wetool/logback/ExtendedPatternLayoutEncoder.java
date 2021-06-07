package org.code4everything.wetool.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pantao
 * @since 2021/6/7
 */
@Slf4j
public class ExtendedPatternLayoutEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        log.info("register logback pid converter");
        PatternLayout.defaultConverterMap.put("pid", PidConverter.class.getName());
        super.start();
    }
}
