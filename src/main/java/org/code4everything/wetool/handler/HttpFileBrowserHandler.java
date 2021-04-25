package org.code4everything.wetool.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import org.code4everything.wetool.plugin.support.exception.HttpException;
import org.code4everything.wetool.plugin.support.http.HttpApiHandler;
import org.code4everything.wetool.plugin.support.http.HttpService;
import org.code4everything.wetool.plugin.support.http.Https;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author pantao
 * @since 2021/4/25
 */
@Data
public class HttpFileBrowserHandler implements HttpApiHandler {

    private final int port;

    private final String apiPattern;

    private final String rootPath;

    private final String patternPrefix;

    public HttpFileBrowserHandler(String apiPattern, String rootPath) {
        this(HttpService.getDefaultPort(), apiPattern, rootPath);
    }

    public HttpFileBrowserHandler(int port, String apiPattern, String rootPath) {
        Objects.requireNonNull(apiPattern);
        this.port = port;
        this.apiPattern = apiPattern;
        this.rootPath = StrUtil.removeSuffix(rootPath, File.separator);
        this.patternPrefix = StrUtil.removeSuffix(apiPattern, "*");
    }

    @Override
    public Object handleApi(HttpRequest httpRequest, FullHttpResponse fullHttpResponse, JSONObject params, JSONObject body) {
        String filePath = StrUtil.removePrefix(params.get(HttpService.REQ_API_KEY).toString(), patternPrefix);
        String absolutePath = Paths.get(rootPath, filePath.split("/")).toAbsolutePath().normalize().toString();
        File file = FileUtil.file(absolutePath);

        if (!file.exists()) {
            throw new HttpException().setStatus(HttpResponseStatus.NOT_FOUND).setMsg("未找到文件：" + filePath);
        }

        if (file.isDirectory()) {
            // TODO: 2021/4/25 文件列表
            return "[1,2]";
        }

        if (params.containsKey("download") || params.containsKey("dl")) {
            return Https.responseFile(fullHttpResponse, absolutePath);
        }
        return Https.responseMedia(fullHttpResponse, absolutePath);
    }

    public void export() {
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
