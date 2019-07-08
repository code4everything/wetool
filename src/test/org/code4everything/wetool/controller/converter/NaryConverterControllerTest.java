package org.code4everything.wetool.controller.converter;

import org.junit.Test;

public class NaryConverterControllerTest {

    @Test
    public void byte2BinaryString() {
        byte b = -2;
        System.out.println(Integer.toBinaryString((b & 0xff) + 0x100).substring(1));
    }

    @Test
    public void byte2HexString() {
        byte b = -2;
        System.out.println(Integer.toHexString((b & 0xff) + 0x100).substring(1));
    }
}
