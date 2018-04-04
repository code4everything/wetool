package com.zhazhapan.util.visual.model;

import com.zhazhapan.util.visual.controller.*;

/**
 * @author pantao
 * @since 2018/3/31
 */
public class ControllerModel {

    private static JsonParserController jsonParserController = null;

    private static FileManagerController fileManagerController = null;

    private static ClipboardHistoryController clipboardHistoryController = null;

    private static MainController mainController = null;

    private static QrCodeGeneratorController qrCodeGeneratorController = null;

    public static QrCodeGeneratorController getQrCodeGeneratorController() {
        return qrCodeGeneratorController;
    }

    public static void setQrCodeGeneratorController(QrCodeGeneratorController qrCodeGeneratorController) {
        ControllerModel.qrCodeGeneratorController = qrCodeGeneratorController;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        ControllerModel.mainController = mainController;
    }

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
