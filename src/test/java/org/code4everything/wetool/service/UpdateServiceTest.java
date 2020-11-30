package org.code4everything.wetool.service;

import cn.hutool.core.io.FileUtil;
import org.code4everything.boot.base.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class UpdateServiceTest {

    private final UpdateService updateService = new UpdateService();

    @Test
    public void testGetNewestVersionInfo() {
        String history = FileUtil.readUtf8String(FileUtils.currentWorkDir("history.md"));
        UpdateService.VersionInfo versionInfo = updateService.getNewestVersionInfo(history);
        Assert.assertNotNull(versionInfo);
    }
}
