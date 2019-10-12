package org.code4everything.wetool.util;

import cn.hutool.core.io.FileUtil;
import javafx.scene.Node;
import lombok.experimental.UtilityClass;
import org.code4everything.wetool.constant.FileConsts;
import org.code4everything.wetool.plugin.support.BaseViewController;
import org.code4everything.wetool.plugin.support.constant.AppConsts;
import org.code4everything.wetool.plugin.support.factory.BeanFactory;
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

    public static void openPluginFolder() {
        FileUtil.mkdir(FileConsts.PLUGIN_FOLDER);
        FxUtils.openFile(FileConsts.PLUGIN_FOLDER);
    }

    public static <T extends BaseViewController> T getView(String tabName) {
        String key = AppConsts.Title.APP_TITLE + tabName;
        T value = BeanFactory.getViewObject(key);
        WeUtils.printDebug("value of key[{}] is: ", key, value);
        return value;
    }

    public static void registerView(String tabName, BaseViewController viewController) {
        BeanFactory.registerView(AppConsts.Title.APP_TITLE, tabName, viewController);
    }

    public static void openTab(Node tabContent, String tabName) {
        FxUtils.openTab(tabContent, AppConsts.Title.APP_TITLE, tabName, null);
    }
}
