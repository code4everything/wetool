package org.code4everything.wetool.util;

import org.code4everything.wetool.config.WeConfig;
import org.code4everything.wetool.factory.BeanFactory;
import org.junit.Test;

public class WeUtilsTest {

    @Test
    public void compressString() {
        BeanFactory.register(new WeConfig());
        String test = "a         b\r\nc\rd\ne";
        assert "a b c d e".equals(WeUtils.compressString(test));
    }
}
