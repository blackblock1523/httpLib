package com.blackblock.http.listener;

import com.blackblock.http.model.Request;

public interface IRequestListener {

    void execute(Request request, IResultListener listener);
}
