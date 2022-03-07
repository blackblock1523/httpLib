package com.blackblock.http.factory;

import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;
import com.blackblock.http.model.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 16:55
 * @desc 文件下载请求
 */
public class DownloadRequest extends BaseRequest {

    public DownloadRequest(Request request, IResultListener resultListener) {
        super(request, resultListener);
    }

    @Override
    public void execute() {
        URL url;

        try {
            url = new URL(request.getUrl());

            initHttpConfig(url);
            // default http header
            httpConn.addRequestProperty("Content-Type", "application/octet-stream");
            httpConn.addRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
                            "application/x-shockwave-flash, application/xaml+xml, " +
                            "application/vnd.ms-xpsdocument, application/x-ms-xbap, " +
                            "application/x-ms-application, application/vnd.ms-excel, " +
                            "application/vnd.ms-powerpoint, application/msword, */*");
            httpConn.addRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.addRequestProperty("Charset", "UTF-8");
            // user custom header
            initHeader();

            httpConn.connect();

            onResponse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResponse() throws IOException {
        int responseCode = httpConn.getResponseCode();
        if (responseCode == -1) {
            /**
             * 存在以下几种情况：
             * 1、没有申请网络权限——已经申请权限
             * 2、没有在子线程中进行网络请求——已经开启异步线程执行的网络请求
             * 3、connection需要进行disconnect——在代码中添加connection.disconnect()后getResponseCode() 还是返回-1
             * 4、请求字符串中存在空格字符
             */
            throw new IOException("Could not retrieve response code from httpUrlConnection.");
        }

        InputStream is = null;
        FileOutputStream fos = null;

        try {
            is = httpConn.getInputStream();
            fos = new FileOutputStream(request.getDownloadFilePath());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read()) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();

            resultListener.onSuccess(new Response(responseCode, request.getDownloadFilePath()));
        } catch (IOException ex) {
            ex.printStackTrace();
            String error = getResponseContent(httpConn.getErrorStream());
            resultListener.onFailed(ex, new Response(responseCode, error));
        } finally {
            if (fos != null) {
                fos.close();
            }

            if (is != null) {
                is.close();
            }
        }
    }
}
