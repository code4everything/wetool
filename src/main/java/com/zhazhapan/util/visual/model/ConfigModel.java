package com.zhazhapan.util.visual.model;

import com.alibaba.fastjson.JSONArray;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author pantao
 * @since 2018/4/2
 */
public class ConfigModel {

    private static double width = 1000;

    private static double height = 700;

    private static JSONArray tabs = new JSONArray();

    private static String fileFilterRegex = "^[^.].*$";

    private static boolean fileFilterTip = true;

    private static int clipboardSize = 20;

    public static Queue<String> clipboardHistoryQueue = new PriorityQueue<>();

    public static int getClipboardSize() {
        return clipboardSize;
    }

    public static void setClipboardSize(int clipboardSize) {
        ConfigModel.clipboardSize = clipboardSize;
    }

    static {
        tabs.add("JsonParser");
        tabs.add("FileManager");
        tabs.add("RandomGenerator");
        tabs.add("ClipboardHistory");
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
