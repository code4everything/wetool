package org.code4everything.wetool.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.code4everything.wetool.plugin.support.util.WeUtils;

/**
 * @author pantao
 * @since 2021/6/7
 */
public class PidConverter extends ClassicConverter {

    private static final String PID = String.valueOf(WeUtils.getCurrentPid());

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return PID;
    }
}
