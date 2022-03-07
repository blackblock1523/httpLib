package com.blackblock.http.factory;

import com.blackblock.http.model.Request;
import com.blackblock.http.model.Response;
import com.blackblock.http.listener.IRequest;
import com.blackblock.http.listener.IResultListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 16:54
 * @desc 请求抽象类
 */
public abstract class BaseRequest implements IRequest {

    protected HttpURLConnection httpConn = null;
    protected Request request;
    protected IResultListener resultListener;


    public BaseRequest(Request request, IResultListener resultListener) {
        this.request = request;
        this.resultListener = resultListener;
    }

    @Override
    public void execute() {

    }

    protected void initHttpConfig(URL url) throws IOException {
        httpConn = (HttpURLConnection) url.openConnection(); //打开http连接
        httpConn.setConnectTimeout(request.getConnectTimeOut()); //超时时间
        httpConn.setReadTimeout(request.getReadTimeOut());
        httpConn.setUseCaches(false); //不用缓存
        httpConn.setInstanceFollowRedirects(true); // 是否启用url重定向
        httpConn.setDoInput(true); //允许读入
        httpConn.setDoOutput(true); //允许写出
        httpConn.setRequestMethod(request.getMethod());
    }

    /**
     * 设置请求头 不要放在{@link #initHttpConfig}里面 用于覆盖默认http请求头
     */
    protected void initHeader() {
        if (request.getHeaders() != null && request.getHeaders().size() > 0) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                httpConn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

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

        try {
            String response = getResponseContent(httpConn.getInputStream());
            resultListener.onSuccess(new Response(responseCode, response));
        } catch (IOException ex) {
            ex.printStackTrace();
            String error = getResponseContent(httpConn.getErrorStream());
            resultListener.onFailed(ex, new Response(responseCode, error));
        }
    }

    protected String getResponseContent(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            inputStream.close();
        }

        return sb.toString();
    }
}
