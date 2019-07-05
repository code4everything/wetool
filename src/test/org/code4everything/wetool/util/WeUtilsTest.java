package org.code4everything.wetool.util;

import org.junit.Test;

public class WeUtilsTest {

    @Test
    public void compressString() {
        String test = "a         b\r\nc\rd\ne";
        assert "a b c d e".equals(WeUtils.compressString(test));
    }
}
