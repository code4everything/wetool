package org.code4everything.wetool.util;

import cn.hutool.core.io.FileUtil;
import lombok.experimental.UtilityClass;
import org.code4everything.wetool.constant.FileConsts;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;

/**
 * @author pantao
 * @since 2019/9/25
 */
@UtilityClass
public class FinalUtils {

    public static void openConfig() {
        FxUtils.openFile(WeUtils.getConfig().getCurrentPath());
    }

    public void openPluginFolder() {
        FileUtil.mkdir(FileConsts.PLUGIN_FOLDER);
        FxUtils.openFile(FileConsts.PLUGIN_FOLDER);
    }
}
