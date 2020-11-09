package org.code4everything.wetool.controller;

import cn.hutool.core.io.FileUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.boot.base.FileUtils;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.plugin.PluginLoader;
import org.code4everything.wetool.plugin.WePlugin;
import org.code4everything.wetool.plugin.support.control.cell.UnmodifiableTextFieldTableCell;
import org.code4everything.wetool.plugin.support.util.FxUtils;

import java.io.File;
import java.util.List;

/**
 * @author pantao
 * @since 2020/11/6
 */
@Slf4j
public class PluginManagerController {

    @FXML
    public TableColumn<WePlugin, String> name;

    @FXML
    public TableColumn<WePlugin, String> author;

    @FXML
    public TableColumn<WePlugin, String> version;

    @FXML
    public TableColumn<WePlugin, String> loaderName;

    @FXML
    public TableColumn<WePlugin, String> path;

    @FXML
    public TableView<WePlugin> pluginTable;

    @FXML
    private void initialize() {
        loaderName.setCellValueFactory(new PropertyValueFactory<>(null) {
            @Override
            @SneakyThrows
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WePlugin, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getClassLoader().getName());
            }
        });
        path.setCellValueFactory(new PropertyValueFactory<>(null) {
            @Override
            @SneakyThrows
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WePlugin, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getJarFile().getAbsolutePath());
            }
        });
        path.setCellFactory(UnmodifiableTextFieldTableCell.forTableColumn());
        name.setCellValueFactory(new PropertyValueFactory<>(null) {
            @Override
            @SneakyThrows
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WePlugin, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getPluginInfo().getName());
            }
        });
        author.setCellValueFactory(new PropertyValueFactory<>(null) {
            @Override
            @SneakyThrows
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WePlugin, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getPluginInfo().getAuthor());
            }
        });
        version.setCellValueFactory(new PropertyValueFactory<>(null) {
            @Override
            @SneakyThrows
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WePlugin, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getPluginInfo().getVersion());
            }
        });

        pluginTable.getItems().addAll(PluginLoader.LOADED_PLUGINS);
    }

    public void addPlugin() {
        FxUtils.chooseFile(file -> {
            String workDir = FileUtils.currentWorkDir("plugins");
            File pluginFolder = FileUtil.mkdir(workDir);
            FileUtil.move(file, pluginFolder, true);
            PluginLoader.loadPlugins(List.of(FileUtil.file(workDir, file.getName())), true);
        });
        pluginTable.getItems().clear();
        pluginTable.getItems().addAll(PluginLoader.LOADED_PLUGINS);
        pluginTable.refresh();
    }

    public void seePluginRepo() {
        FxUtils.openLink(TipConsts.REPO_LINK);
    }
}
