package org.code4everything.wetool.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * @author pantao
 * @since 2021/6/7
 */
public class ExtendedPatternLayoutEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        PatternLayout.defaultConverterMap.put("pid", PidConverter.class.getName());
        super.start();
    }
}
