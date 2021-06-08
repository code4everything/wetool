package org.code4everything.wetool.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.comparator.ComparatorChain;
import org.code4everything.wetool.plugin.support.exception.PluginException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author pantao
 * @since 2021/6/8
 */
public class AppNameConverter extends ClassicConverter {

    private static final Map<String, String> PLUGIN_NAME_MAP = new TreeMap<>(ComparatorChain.of((o1, o2) -> o2.split("\\.").length - o1.split("\\.").length, String::compareTo));

    static {
        PLUGIN_NAME_MAP.put("org.code4everything.wetool", "wetool");
    }

    public static void putName(String packageName, String appName) {
        if (PLUGIN_NAME_MAP.containsKey(packageName)) {
            throw new PluginException("包已存在：" + packageName);
        }
        PLUGIN_NAME_MAP.put(packageName, appName);
    }

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        String loggerName = iLoggingEvent.getLoggerName();
        for (Entry<String, String> entry : PLUGIN_NAME_MAP.entrySet()) {
            if (loggerName.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "wetool";
    }
}
