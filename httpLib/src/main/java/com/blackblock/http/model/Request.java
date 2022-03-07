package com.blackblock.http.model;

import com.blackblock.http.util.RequestType;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    private String url;
    // 请求方法
    private String method;
    // 连接超时时间
    private int connectTimeOut;
    // 读取超时时间
    private int readTimeOut;
    // 编码
    private boolean isEncode;
    // http包请求头
    private Map<String, String> headers;
    // http包请求参数
    private Map<String, String> params;
    private RequestType requestType;
    // 上传文件
    private List<File> files;
    // 下载文件路径
    private String downloadFilePath;

    public Request(creator creator) {
        this.url = creator.url;
        this.method = creator.method;
        this.connectTimeOut = creator.connectTimeOut;
        this.readTimeOut = creator.readTimeOut;
        this.isEncode = creator.isEncode;
        this.headers = creator.headers;
        this.params = creator.params;
        this.requestType = creator.requestType;
        this.files = creator.files;
        this.downloadFilePath = creator.downloadFilePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public boolean isEncode() {
        return isEncode;
    }

    public void setEncode(boolean encode) {
        isEncode = encode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Request setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Request setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public Request setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
        return this;
    }

    public Request addParam(String key, String val) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public String getRequestBodyByString() {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key == null) {
                    continue;
                }
                if (value == null) {
                    value = "";
                }
                if (isEncode) {
                    try {
                        sb.append(URLEncoder.encode(key, "UTF-8"))
                                .append("=")
                                .append(URLEncoder.encode(value, "UTF-8"))
                                .append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    sb.append(key)
                            .append("=")
                            .append(value)
                            .append("&");
                }
            }
            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        } else {
            return "";
        }
    }


    public String getRequestBodyByJSON() {
        if (params != null) {
            return new JSONObject(params).toString();
        } else {
            return "";
        }
    }

    public static class creator {

        private String url;
        // 请求方法
        private String method;
        // 连接超时时间
        private int connectTimeOut = 6000;
        // 读取超时时间
        private int readTimeOut = 3000;
        // 编码
        private boolean isEncode = true;
        // http包请求头
        private Map<String, String> headers;
        // http包请求参数
        private Map<String, String> params = new HashMap<>();
        private RequestType requestType = RequestType.String;
        // 上传文件
        private List<File> files = new ArrayList();
        // 下载文件路径
        private String downloadFilePath;


        public creator() {
        }

        public creator setUrl(String url) {
            this.url = url;
            return this;
        }

        public creator setMethod(String method) {
            this.method = method;
            return this;
        }

        public creator setConnectTimeOut(int connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        public creator setReadTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public creator setEncode(boolean encode) {
            isEncode = encode;
            return this;
        }

        public creator setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public creator setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public creator setRequestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public creator setFiles(List<File> files) {
            this.files = files;
            return this;
        }

        public creator setDownloadFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
            return this;
        }

        public Request create() {

            return new Request(this);
        }
    }

}
