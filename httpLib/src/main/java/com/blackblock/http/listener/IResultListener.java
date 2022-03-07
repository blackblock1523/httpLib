package com.blackblock.http.listener;

import com.blackblock.http.model.Response;

public interface IResultListener {

    void onSuccess(Response response);

    void onFailed(Exception ex, Response response);

}
