package com.blackblock.http.factory;

import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;
import com.blackblock.http.model.Response;
import com.blackblock.http.util.RequestType;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 17:03
 * @desc 普通http请求 post get等
 */
public class StringRequest extends BaseRequest {

    public StringRequest(Request request, IResultListener resultListener) {
        super(request, resultListener);
    }

    @Override
    public void execute() {
        URL url;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        try {
            url = new URL(request.getUrl());
            initHttpConfig(url);
            // user custom header
            initHeader();
            httpConn.connect();

            os = httpConn.getOutputStream();
            bos = new BufferedOutputStream(os);

            if (Request.POST.equals(request.getMethod())) {
                if (request.getRequestType() == RequestType.JSON) {
                    bos.write(request.getRequestBodyByJSON().getBytes(UTF_8));
                } else {
                    bos.write(request.getRequestBodyByString().getBytes(UTF_8));
                }
            }

            bos.flush();

            onResponse();
        } catch (IOException ex) {
            ex.printStackTrace();
            resultListener.onFailed(ex, new Response(0, null));
        } finally {
            try {
                if (os != null) os.close();
            } catch (Exception e) {

            }
            try {
                if (bos != null) bos.close();
            } catch (Exception e) {

            }
            httpConn.disconnect();
        }
    }
}
