package com.zhazhapan.util.visual.model;

import cn.hutool.core.swing.ClipboardUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import javafx.util.Pair;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 配置参数
 *
 * @author pantao
 * @since 2018/4/2
 */
public class ConfigModel {

    private static List<Pair<Date, String>> clipboardHistory = new LinkedList<>();

    private static double width = 1000;

    private static double height = 700;

    private static JSONArray supportTabs;

    private static String fileFilterRegex = "^[^.].*$";

    private static boolean fileFilterTip = true;

    private static int clipboardSize = 20;

    private static JSONArray tabs = new JSONArray();

    private static boolean fullscreen = false;

    private static boolean autoWrap = false;

    static {
        tabs.add("JsonParser");
        tabs.add("FileManager");
        tabs.add("RandomGenerator");
        tabs.add("ClipboardHistory");
        tabs.add("QrCodeGenerator");
        tabs.add("CharsetConverter");
        tabs.add("NetworkTool");
        supportTabs = ObjectUtil.cloneByStream(tabs);
    }

    public static JSONArray getSupportTabs() {
        return supportTabs;
    }

    public static boolean isAutoWrap() {
        return autoWrap;
    }

    public static void setAutoWrap(boolean autoWrap) {
        ConfigModel.autoWrap = autoWrap;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean fullscreen) {
        ConfigModel.fullscreen = fullscreen;
    }

    public static int getClipboardHistorySize() {
        return clipboardHistory.size();
    }

    public static void appendClipboardHistory(Date date, String content) {
        if (getClipboardHistorySize() < clipboardSize) {
            clipboardHistory.add(new Pair<>(date, Checker.checkNull(content)));
        } else {
            clipboardHistory.remove(ValueConsts.ZERO_INT);
            appendClipboardHistory(date, content);
        }
    }

    public static Pair<Date, String> getLastClipboardHistoryItem() {
        Optional<Pair<Date, String>> last = Optional.ofNullable(getClipboardHistoryItem(getClipboardHistorySize() - 1));
        return last.orElse(new Pair<>(new Date(), Checker.checkNull(ClipboardUtil.getStr())));
    }

    public static Pair<Date, String> getClipboardHistoryItem(int index) {
        if (index >= 0 && index < clipboardSize) {
            return clipboardHistory.get(index);
        }
        return null;
    }

    public static void setClipboardSize(int clipboardSize) {
        ConfigModel.clipboardSize = clipboardSize;
    }

    public static double getWidth() {
        return width;
    }

    public static void setWidth(double width) {
        ConfigModel.width = width;
    }

    public static double getHeight() {
        return height;
    }

    public static void setHeight(double height) {
        ConfigModel.height = height;
    }

    public static JSONArray getTabs() {
        return tabs;
    }

    public static void setTabs(JSONArray tabs) {
        ConfigModel.tabs = tabs;
    }

    public static String getFileFilterRegex() {
        return fileFilterRegex;
    }

    public static void setFileFilterRegex(String fileFilterRegex) {
        ConfigModel.fileFilterRegex = fileFilterRegex;
    }

    public static boolean isFileFilterTip() {
        return fileFilterTip;
    }

    public static void setFileFilterTip(boolean fileFilterTip) {
        ConfigModel.fileFilterTip = fileFilterTip;
    }
}
