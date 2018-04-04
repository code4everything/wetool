package com.zhazhapan.util.visual;

import com.zhazhapan.config.JsonParser;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;

/**
 * @author pantao
 * @since 2018/4/2
 */
public class ConfigParser {

    private static final String FILE_FILTER_TIP_PATH = "fileFilter.showTip";

    private static final String WIDTH_PATH = "initialize.width";

    private static final String HEIGHT_PATH = "initialize.height";

    private static final String FILE_REGEX_PATH = "fileFilter.regex";

    private static final String TAB_PATH = "initialize.tabs.load";

    private static final String CLIPBOARD_SIZE_PATH = "clipboardSize";

    private static final String FULLSCREEN = "initialize.fullscreen";

    public static void parserConfig() {
        try {
            JsonParser parser = new JsonParser(FileExecutor.read(WeToolApplication.class.getResourceAsStream
                    (LocalValueConsts.CONFIG_PATH)), ValueConsts.TRUE);
            ConfigModel.setWidth(parser.getDoubleUseEval(WIDTH_PATH));
            ConfigModel.setHeight(parser.getDoubleUseEval(HEIGHT_PATH));
            ConfigModel.setTabs(parser.getArray(TAB_PATH));
            ConfigModel.setFileFilterRegex(parser.getString(FILE_REGEX_PATH));
            ConfigModel.setFileFilterTip(parser.getBooleanUseEval(FILE_FILTER_TIP_PATH));
            ConfigModel.setClipboardSize(parser.getIntegerUseEval(CLIPBOARD_SIZE_PATH));
            ConfigModel.setFullscreen(parser.getBooleanUseEval(FULLSCREEN));
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.LOAD_CONFIG_ERROR);
        }
    }
}
