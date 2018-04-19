package com.zhazhapan.util.visual;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhazhapan.config.JsonParser;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.FileExecutor;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.WaverModel;

import java.util.List;

/**
 * @author pantao
 * @since 2018/4/2
 */
class ConfigParser {

    private static final String FILE_FILTER_TIP_PATH = "fileFilter.showTip";

    private static final String WIDTH_PATH = "initialize.width";

    private static final String HEIGHT_PATH = "initialize.height";

    private static final String FILE_REGEX_PATH = "fileFilter.regex";

    private static final String TAB_PATH = "initialize.tabs.load";

    private static final String CLIPBOARD_SIZE_PATH = "clipboardSize";

    private static final String FULLSCREEN = "initialize.fullscreen";

    private static final String AUTO_WRAP = "autoWrap";

    private static final String MYSQL_HOST = "mysql.host";

    private static final String MYSQL_DB = "mysql.database";

    private static final String MYSQL_CONDITION = "mysql.condition";

    private static final String MYSQL_USERNAME = "mysql.username";

    private static final String MYSQL_PASSWORD = "mysql.password";

    private static final String WAVE = "waveViewer";

    private static final String TABLE_NAME = "tableName";

    private static final String DATA_FIELD = "dataField";

    private static final String DATE_FIELD = "dateField";

    private static final String TITLE = "title";

    private static final String RESULT_SIZE = "firstResultSize";

    static void parserConfig() {
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
            ConfigModel.setAutoWrap(parser.getBooleanUseEval(AUTO_WRAP));
            ConfigModel.setHost(parser.getStringUseEval(MYSQL_HOST));
            ConfigModel.setDatabase(parser.getStringUseEval(MYSQL_DB));
            ConfigModel.setCondition(parser.getStringUseEval(MYSQL_CONDITION));
            ConfigModel.setUsername(parser.getStringUseEval(MYSQL_USERNAME));
            ConfigModel.setPassword(parser.getStringUseEval(MYSQL_PASSWORD));
            JSONArray array = parser.getJsonObject().getJSONArray(WAVE);
            List<WaverModel> waves = ConfigModel.getWaver();
            array.forEach(obj -> {
                JSONObject object = (JSONObject) obj;
                WaverModel model = new WaverModel();
                model.setTableName(object.getString(TABLE_NAME));
                model.setDataField(object.getString(DATA_FIELD));
                model.setDateField(object.getString(DATE_FIELD));
                model.setTitle(object.getString(TITLE));
                model.setFirstResultSize(object.getInteger(RESULT_SIZE));
                waves.add(model);
            });
        } catch (Exception e) {
            Alerts.showError(LocalValueConsts.MAIN_TITLE, LocalValueConsts.LOAD_CONFIG_ERROR);
        }
    }
}
