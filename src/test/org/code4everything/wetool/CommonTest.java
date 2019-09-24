package org.code4everything.wetool;

import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;

/**
 * @author pantao
 * @since 2019/9/24
 */
public class CommonTest {

    @Test
    public void testReplaceFileSeparator() {
        String test1 = "parent\\child";
        String test2 = "parent/child";
        String ans = "parent" + File.separator + "child";
        assert ans.equals(test1.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        assert ans.equals(test2.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
    }
}
