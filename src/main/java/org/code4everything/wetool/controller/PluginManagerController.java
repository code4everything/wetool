package org.code4everything.wetool.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.constant.TipConsts;
import org.code4everything.wetool.plugin.PluginLoader;
import org.code4everything.wetool.plugin.WePlugin;
import org.code4everything.wetool.plugin.support.control.cell.UnmodifiableTextFieldTableCell;
import org.code4everything.wetool.plugin.support.util.FxDialogs;
import org.code4everything.wetool.plugin.support.util.FxUtils;
import org.code4everything.wetool.plugin.support.util.WeUtils;
import org.code4everything.wetool.util.FinalUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

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
            File pluginFolder = WeUtils.getPluginFolder();
            FileUtil.move(file, pluginFolder, true);
            loadPlugin(FileUtil.file(pluginFolder.getAbsolutePath(), file.getName()));
        });
    }

    public void seePluginRepo() {
        FxUtils.openLink(TipConsts.REPO_LINK);
    }

    public void installPlugin() {
        FxDialogs.showTextInput("插件下载", "插件地址", url -> {
            if (StrUtil.isBlank(url)) {
                return;
            }
            File pluginFolder = WeUtils.getPluginFolder();
            File plugin = HttpUtil.downloadFileFromUrl(url, pluginFolder);
            if (isZip(plugin.getName())) {
                ZipUtil.unzip(plugin, pluginFolder);
                try (ZipFile zipFile = new ZipFile(plugin)) {
                    ZipUtil.listFileNames(zipFile, "").forEach(e -> loadPlugin(FileUtil.file(pluginFolder, e)));
                } catch (IOException e) {
                    FxDialogs.showException("加载插件异常", e);
                }
                FileUtil.del(plugin);
            } else {
                loadPlugin(plugin);
            }
        });
    }

    private void loadPlugin(File plugin) {
        PluginLoader.loadPlugins(List.of(plugin), true);
        pluginTable.getItems().clear();
        pluginTable.getItems().addAll(PluginLoader.LOADED_PLUGINS);
        pluginTable.refresh();
    }

    private boolean isZip(String filename) {
        return filename.endsWith(".zip");
    }

    public void openPluginFolder() {
        FinalUtils.openPluginFolder();
    }
}
