package org.code4everything.wetool.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import javafx.application.Platform;
import org.code4everything.wetool.plugin.support.http.HttpApiHandler;
import org.code4everything.wetool.plugin.support.http.ObjectResp;
import org.code4everything.wetool.plugin.support.util.WeUtils;

/**
 * @author pantao
 * @since 2020/12/6
 */
public class ExitHttpApiHandler implements HttpApiHandler {

    @Override
    public Object handleApi(HttpRequest httpRequest, HttpResponse httpResponse, JSONObject params, JSONObject body) {
        Platform.runLater(WeUtils::exitSystem);
        return ObjectResp.of("status", "success");
    }
}
