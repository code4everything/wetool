package org.code4everything.wetool;

import org.code4everything.boot.config.BootConfig;
import org.code4everything.wetool.plugin.support.http.HttpService;

/**
 * @author pantao
 * @since 2019/8/24
 */
public class WeApplicationTest {

    public static void main(String[] args) {
        HttpService.setDefaultPort(58189);
        BootConfig.setDebug(true);
        WeApplication.main(args);
    }
}
