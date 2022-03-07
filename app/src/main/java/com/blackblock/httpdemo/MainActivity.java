package com.blackblock.httpdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blackblock.http.HttpRequestManager;
import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;
import com.blackblock.http.model.Response;
import com.blackblock.http.util.RequestType;
import com.blackblock.http.util.ThreadPoolManager;

import java.util.HashMap;
import java.util.Map;

import static com.blackblock.http.model.Request.GET;
import static com.blackblock.http.model.Request.POST;

public class MainActivity extends AppCompatActivity {

    String getUrl = "https://silkroad.csdn.net/api/v2/assemble/list/channel/search_hot_word";
    String postUrl = "https://ug.baidu.com/mcp/pc/pcsearch";

    private Map<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGet(View v) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getRequest();
            }
        });

    }

    public void onPost(View v) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                postRequest();
            }
        });

    }

    private void postRequest() {
        map.put("keyword", "通过Volley我们能学到什么?(2) — 刨析网络请求框架");
        map.put("page_no", "1");
        map.put("decode", "1");

        Request request = new Request.creator()
                .setUrl(postUrl)
                .setMethod(POST)
                .setEncode(true)
                .setRequestType(RequestType.JSON)
                .setParams(cast2MapString(map))
                .create();

        HttpRequestManager.getInstance().execute(request, getListener());
    }

    private void getRequest() {
        map.put("channel_name", "pc_hot_word");
        map.put("size", "10");
        map.put("platform", "pc");
        map.put("imei", "10_30706508050-1645611712777-236911");
        map.put("toolbarSearchExt", "{\"landingWord\":[],\"queryWord\":\"\",\"tag\":[\"Volley\",\"网络\",\"框架\",\"fragment\"],\"title\":\"通过Volley我们能学到什么?(2) \n" +
                "mdash; 刨析网络请求框架\"}");

        Request request = new Request.creator()
                .setUrl(getUrl)
                .setMethod(GET)
                .setEncode(true)
                .setRequestType(RequestType.JSON)
                .setParams(cast2MapString(map))
                .create();

        HttpRequestManager.getInstance().execute(request, getListener());
    }

    private IResultListener getListener() {
        return new IResultListener() {
            @Override
            public void onSuccess(Response response) {
                Log.e("http", "code: " + response.getCode());
                Log.e("http", "result: " + response.getResult());
            }

            @Override
            public void onFailed(Exception ex, Response response) {
                Log.e("http", "ex: " + ex.getMessage());
                Log.e("http", "code: " + response.getResult());
                Log.e("http", "result: " + response.getResult());
            }
        };
    }

    private Map<String, String> cast2MapString(Map<String, Object> map) {
        Map<String, String> temp = new HashMap<>();
        for (String key : map.keySet()) {
            temp.put(key, map.get(key).toString());
        }
        map.clear();

        return temp;
    }
}