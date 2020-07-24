package org.code4everything.wetool;

import cn.hutool.core.util.StrUtil;
import org.code4everything.wetool.constant.ViewConsts;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author pantao
 * @since 2019/9/24
 */
public class CommonTest {

    @Test
    public void testMapIterator() {
        Map<String, String> map = new HashMap<>(8);
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        Assert.assertFalse(map.isEmpty());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testReplaceFileSeparator() {
        String test1 = "parent\\child";
        String test2 = "parent/child";
        String ans = "parent" + File.separator + "child";
        Assert.assertEquals(ans, test1.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        Assert.assertEquals(ans, test2.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
    }

    @Test
    public void testUrl() {
        Assert.assertTrue(StrUtil.isNotEmpty(CommonTest.class.getResource(ViewConsts.MAIN).toString()));
    }
}
