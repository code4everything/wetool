package org.code4everything.wetool.constant;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.code4everything.boot.base.FileUtils;

import java.io.File;

/**
 * @author pantao
 * @since 2019/9/25
 */
@UtilityClass
public class FileConsts {

    public static final String PLUGIN_FOLDER = FileUtils.currentWorkDir("plugins");

    public static final String LOG_FOLDER = StrUtil.join(File.separator, FileUtil.getUserHomePath(), "logs", "wetool");

    public static final String LOG = LOG_FOLDER + File.separator + "wetool.log";
}
