package org.code4everything.wetool;

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
        assert !map.isEmpty();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        assert map.isEmpty();
    }

    @Test
    public void testReplaceFileSeparator() {
        String test1 = "parent\\child";
        String test2 = "parent/child";
        String ans = "parent" + File.separator + "child";
        assert ans.equals(test1.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        assert ans.equals(test2.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
    }
}
