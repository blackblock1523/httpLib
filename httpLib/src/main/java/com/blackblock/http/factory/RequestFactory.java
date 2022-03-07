package com.blackblock.http.factory;

import com.blackblock.http.listener.IRequest;
import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 16:53
 * @desc 请求工厂 统一管理不同类型的请求
 */
public class RequestFactory {

    private static RequestFactory instance;

    private RequestFactory() {
    }

    public static RequestFactory getInstance() {
        if (instance == null) {
            instance = new RequestFactory();
        }
        return instance;
    }

    public IRequest CreateRequest(Request request, IResultListener listener) {
        switch (request.getRequestType()) {
            case String:
            case JSON:
                return new StringRequest(request, listener);
            case Download:
                return new DownloadRequest(request, listener);
            case FileUpload:
                return new FileRequest(request, listener);
            default:
                return null;
        }
    }

}
