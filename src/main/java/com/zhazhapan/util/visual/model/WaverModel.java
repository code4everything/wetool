package com.zhazhapan.util.visual.model;

/**
 * @author pantao
 * @since 2018/4/19
 */
public class WaverModel {

    private String title;

    private String tableName;

    private String dataField;

    private String dateField;

    private int firstResultSize;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public String getDateField() {
        return dateField;
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }

    public int getFirstResultSize() {
        return firstResultSize;
    }

    public void setFirstResultSize(int firstResultSize) {
        this.firstResultSize = firstResultSize;
    }
}
