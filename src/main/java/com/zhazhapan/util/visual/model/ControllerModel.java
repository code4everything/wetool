package com.zhazhapan.util.visual.model;

import com.zhazhapan.util.visual.controller.ClipboardHistoryController;
import com.zhazhapan.util.visual.controller.FileManagerController;
import com.zhazhapan.util.visual.controller.JsonParserController;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class ControllerModel {

    private static JsonParserController jsonParserController = null;

    private static FileManagerController fileManagerController = null;

    private static ClipboardHistoryController clipboardHistoryController = null;

    public static ClipboardHistoryController getClipboardHistoryController() {
        return clipboardHistoryController;
    }

    public static void setClipboardHistoryController(ClipboardHistoryController clipboardHistoryController) {
        ControllerModel.clipboardHistoryController = clipboardHistoryController;
    }

    public static FileManagerController getFileManagerController() {
        return fileManagerController;
    }

    public static void setFileManagerController(FileManagerController fileManagerController) {
        ControllerModel.fileManagerController = fileManagerController;
    }

    public static JsonParserController getJsonParserController() {
        return jsonParserController;

    }

    public static void setJsonParserController(JsonParserController jsonParserController) {
        ControllerModel.jsonParserController = jsonParserController;
    }
}
