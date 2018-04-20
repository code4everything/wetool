package com.zhazhapan.util.visual.controller;

import com.zhazhapan.modules.constant.ValueConsts;
import com.zhazhapan.util.Checker;
import com.zhazhapan.util.Formatter;
import com.zhazhapan.util.dialog.Alerts;
import com.zhazhapan.util.visual.WeUtils;
import com.zhazhapan.util.visual.constant.LocalValueConsts;
import com.zhazhapan.util.visual.model.ConfigModel;
import com.zhazhapan.util.visual.model.ControllerModel;
import com.zhazhapan.util.visual.model.WaverModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author pantao
 * @since 2018/4/19
 */
public class WaveController {

    private final String INSERT = "插入";

    private final String DELETE = "删除";

    private final String UPDATE = "更新";

    @FXML
    public ComboBox<String> tableCombo;

    @FXML
    public DatePicker startDate;

    @FXML
    public DatePicker endDate;

    @FXML
    public AreaChart<String, Object> chart;

    @FXML
    public ComboBox<String> crudMethod;

    @FXML
    public TextField sqlNumber;

    @FXML
    public TextField sql;

    @FXML
    public DatePicker sqlDate;

    Connection connection = null;

    Statement statement = null;

    @FXML
    private void initialize() {
        List<WaverModel> waves = ConfigModel.getWaver();
        if (Checker.isNotEmpty(waves)) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://" + ConfigModel.getHost() + "/" + ConfigModel.getDatabase() + "?" +
                        ConfigModel.getCondition();
                connection = DriverManager.getConnection(url, ConfigModel.getUsername(), ConfigModel.getPassword());
                statement = connection.createStatement();
            } catch (Exception e) {
                Platform.runLater(() -> Alerts.showError(LocalValueConsts.MAIN_TITLE, e.getMessage()));
            }
            waves.forEach(waverModel -> tableCombo.getItems().add(waverModel.getTableName()));
            tableCombo.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> getWaveDataToLineChart());
            tableCombo.getSelectionModel().selectFirst();
            crudMethod.getItems().addAll(INSERT, DELETE, UPDATE);
            crudMethod.getSelectionModel().selectFirst();
            crudMethod.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> generateSql());
        }
        ControllerModel.setWaveController(this);
    }

    public void generateSql() {
        WaverModel wave = ConfigModel.getWaver().get(tableCombo.getSelectionModel().getSelectedIndex());
        LocalDate localDate = sqlDate.getValue();
        String date;
        if (Checker.isNull(localDate)) {
            date = Formatter.dateToString(new Date());
        } else {
            date = Formatter.dateToString(Formatter.localDateToDate(localDate));
        }
        switch (crudMethod.getSelectionModel().getSelectedItem()) {
            case INSERT:
                sql.setText("insert into " + wave.getTableName() + "(" + wave.getDataField() + "," + wave
                        .getDateField() + ") values('" + sqlNumber.getText() + "','" + date + "')");
                break;
            case DELETE:
                sql.setText("delete from " + wave.getTableName() + " where " + wave.getDateField() + "='" + date + "'");
                break;
            case UPDATE:
                sql.setText("update " + wave.getTableName() + " set " + wave.getDataField() + "='" + sqlNumber
                        .getText() + "' where " + wave.getDateField() + "='" + date + "'");
            default:
                break;
        }
    }

    public void getWaveDataToLineChart() {
        if (Checker.isNotNull(statement)) {
            chart.getData().clear();
            WaverModel wave = ConfigModel.getWaver().get(tableCombo.getSelectionModel().getSelectedIndex());
            String sql = "select " + wave.getDataField() + "," + wave.getDateField() + " from " + wave.getTableName();
            LocalDate startLocalDate = startDate.getValue();
            LocalDate endLocalDate = endDate.getValue();
            boolean startIsNull = Checker.isNull(startLocalDate);
            boolean endIsNull = Checker.isNull(endLocalDate);
            if (!startIsNull) {
                String start = Formatter.dateToString(Formatter.localDateToDate(startDate.getValue()));
                sql += " where " + wave.getDateField() + ">='" + start + "' and";
            }
            String and = "and";
            if (!endIsNull) {
                String end = Formatter.dateToString(Formatter.localDateToDate(endDate.getValue()));
                if (sql.endsWith(and)) {
                    sql += " " + wave.getDateField() + "<='" + end + "'";
                } else {
                    sql += " where " + wave.getDateField() + "<='" + end + "'";
                }
            }
            if (sql.endsWith(and)) {
                sql = sql.substring(0, sql.length() - 4);
            }
            sql += " order by " + wave.getDateField();
            if (startIsNull && endIsNull) {
                sql += " limit 0," + wave.getFirstResultSize();
            }
            XYChart.Series<String, Object> series = new XYChart.Series<>();
            series.setName(wave.getTitle());
            try {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    String data = resultSet.getString(ValueConsts.ONE_INT);
                    String date = resultSet.getString(ValueConsts.TWO_INT);
                    Object value = data;
                    if (Checker.isDecimal(data)) {
                        if (data.contains(ValueConsts.DOT_SIGN)) {
                            int idx = data.lastIndexOf(ValueConsts.DOT_SIGN) + 3;
                            if (data.length() > idx) {
                                data = data.substring(0, idx);
                            }
                            value = WeUtils.stringToDouble(data);
                        } else {
                            value = WeUtils.stringToInt(data);
                        }
                    }
                    series.getData().add(new XYChart.Data<>(date.split(" ")[0], value));
                }
            } catch (SQLException e) {
                System.out.println(sql);
                Alerts.showError(LocalValueConsts.MAIN_TITLE, e.getMessage());
            }
            chart.getData().add(series);
            chart.setTitle(wave.getTitle());
        }
    }

    public void executeSql() {
        if (Checker.isNotNull(statement)) {
            try {
                statement.executeUpdate(WeUtils.replaceVariable(sql.getText()));
                Alerts.showInformation(LocalValueConsts.MAIN_TITLE, LocalValueConsts.OPERATION_SUCCESS);
                getWaveDataToLineChart();
            } catch (SQLException e) {
                Alerts.showError(LocalValueConsts.MAIN_TITLE, e.getMessage());
            }
        }
    }
}
