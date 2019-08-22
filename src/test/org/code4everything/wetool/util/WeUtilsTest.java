package org.code4everything.wetool.plugin.support.util;

import org.code4everything.wetool.plugin.support.config.WeConfig;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
import org.junit.Test;

public class WeUtilsTest {

    @Test
    public void compressString() {
        BeanFactory.register(new WeConfig());
        String test = "a         b\r\nc\rd\ne";
        assert "a b c d e".equals(WeUtils.compressString(test));
    }
}
