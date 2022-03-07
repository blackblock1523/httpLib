package com.blackblock.http;

import com.blackblock.http.factory.RequestFactory;
import com.blackblock.http.listener.IRequest;
import com.blackblock.http.listener.IRequestListener;
import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;
import com.blackblock.http.util.ThreadPoolManager;

import static com.blackblock.http.model.Request.GET;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 16:53
 * @desc 外部调用方法
 */
public class HttpRequestManager implements IRequestListener {

    private static HttpRequestManager instance;

    private Request request;

    private HttpRequestManager() {
    }

    public static HttpRequestManager getInstance() {
        if (instance == null) {
            instance = new HttpRequestManager();
        }
        return instance;
    }

    @Override
    public void execute(Request request, IResultListener listener) {
        setRequest(request);
        final IRequest iRequest = RequestFactory.getInstance().CreateRequest(this.request, listener);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                iRequest.execute();
            }
        });
    }

    private void setRequest(Request request) {
        this.request = request;
        if (GET.equals(request.getMethod())) {
            String url = request.getUrl();
            url += "?" + request.getRequestBodyByString();
            this.request.setUrl(url);
        }
    }

}
