package org.code4everything.wetool;

import org.code4everything.boot.config.BootConfig;

/**
 * @author pantao
 * @since 2019/8/24
 */
public class WeApplicationTest {

    public static void main(String[] args) {
        BootConfig.setDebug(true);
        WeApplication.main(args);
    }
}
