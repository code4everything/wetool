package org.code4everything.wetool.handler;

import cn.hutool.core.comparator.ComparatorChain;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.plugin.support.exception.HttpException;
import org.code4everything.wetool.plugin.support.http.HttpApiHandler;
import org.code4everything.wetool.plugin.support.http.HttpService;
import org.code4everything.wetool.plugin.support.http.Https;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/**
 * @author pantao
 * @since 2021/4/25
 */
@Data
@Slf4j
public class HttpFileBrowserHandler implements HttpApiHandler {

    private final int port;

    private final String apiPattern;

    private final String rootPath;

    private final String patternPrefix;

    private final String urlPrefix;

    private final Map<Boolean, Integer> directorOrder = Map.of(true, 1, false, 2);

    public HttpFileBrowserHandler(String apiPattern, String rootPath) {
        this(HttpService.getDefaultPort(), apiPattern, rootPath);
    }

    public HttpFileBrowserHandler(int port, String apiPattern, String rootPath) {
        Objects.requireNonNull(apiPattern);
        this.port = port;
        this.apiPattern = apiPattern;
        this.rootPath = StrUtil.removeSuffix(rootPath, File.separator);
        this.patternPrefix = StrUtil.removeSuffix(StrUtil.removeSuffix(apiPattern, "*"), "/");
        int idx = patternPrefix.indexOf("/");
        this.urlPrefix = idx < 0 ? patternPrefix : patternPrefix.substring(idx);
    }

    @Override
    public Object handleApi(HttpRequest httpRequest, FullHttpResponse fullHttpResponse, JSONObject params, JSONObject body) {
        String api = params.get(HttpService.REQ_API_KEY).toString();
        String filePath = StrUtil.strip(StrUtil.removePrefix(api, patternPrefix), "/");
        String absolutePath = StrUtil.removeSuffix(Paths.get(rootPath, filePath.split("/")).toAbsolutePath().normalize().toString(), File.separator);
        File file = FileUtil.file(absolutePath);

        if (!file.exists()) {
            throw new HttpException().setStatus(HttpResponseStatus.NOT_FOUND).setMsg("未找到文件：" + filePath);
        }

        if (file.isDirectory()) {
            log.info("response folder children: {}", file.getAbsolutePath());
            StringBuilder sb = getFileListHtml(filePath, file);
            return Https.responseHtml(fullHttpResponse, sb.toString());
        }
        if (params.containsKey("download") || params.containsKey("dl")) {
            return Https.responseFile(fullHttpResponse, absolutePath);
        }
        return Https.responseMedia(fullHttpResponse, absolutePath);
    }

    private StringBuilder getFileListHtml(String filePath, File file) {
        StringBuilder sb = new StringBuilder("<!DOCTYPE html><html><head><meta charset='utf-8'><title>静态文件浏览服务</title></head><body>");
        sb.append("<h1>当前文件夹：").append(StrUtil.emptyToDefault(filePath, "根目录")).append("</h1><hr/><br/><pre>");

        if (!file.getAbsolutePath().equals(rootPath)) {
            String parentUrl = urlPrefix + StrUtil.removePrefix(file.getParent(), rootPath).replace('\\', '/');
            sb.append("<a href='").append(parentUrl).append("/'>上一级</a><br/><br/>");
        }

        File[] files = file.listFiles();
        Objects.requireNonNull(files);
        Arrays.sort(files, ComparatorChain.of(Comparator.comparing(o -> directorOrder.get(o.isDirectory())), Comparator.comparing(File::lastModified)));

        int maxNameLen = getMaxNameLen(files);
        for (File child : files) {
            String name = child.getName();
            if (child.getAbsolutePath().equals(file.getParent()) || name.startsWith(".")) {
                continue;
            }
            String url = urlPrefix + (StrUtil.isEmpty(filePath) ? "" : "/" + filePath) + "/" + name;
            String txt = DateUtil.formatDateTime(DateUtil.date(child.lastModified())) + "  " + FileUtil.readableFileSize(child);
            sb.append(StrUtil.padAfter(txt, 35, " "));
            if (child.isDirectory()) {
                sb.append("<a href='").append(url).append("'>").append(name).append("</a><br/>");
            } else {
                sb.append("<a href='").append(url).append("' target='_blank'>").append(name).append("<a/>");
                sb.append(StrUtil.repeat(' ', maxNameLen - name.length()));
                sb.append("<a href='").append(url).append("?download='>下载<a/><br/>");
            }
        }

        return sb.append("</pre></body></html>");
    }

    private int getMaxNameLen(File[] files) {
        int maxNameLen = 0;
        for (File child : files) {
            int len = child.getName().length();
            if (len > maxNameLen) {
                maxNameLen = len;
            }
        }
        maxNameLen += 5;
        return maxNameLen;
    }

    public void export() {
        log.info("export http file browser, port {}, api-pattern {}, root-path {}", port, apiPattern, rootPath);
        HttpService.exportHttp(port, apiPattern, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpFileBrowserHandler that = (HttpFileBrowserHandler) o;
        return port == that.port && apiPattern.equals(that.apiPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, apiPattern);
    }
}
