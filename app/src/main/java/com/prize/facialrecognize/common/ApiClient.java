package com.prize.facialrecognize.common;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.prize.facialrecognize.common.Constants.MATCH_URL;

/**
 * Created by Administrator on 2017/3/14.
 */

public class ApiClient {

    private static ApiClient mInstance;
    private OkHttpClient client = null;
    private final static Object mLock = new Object();
    // 超时时间
    public static final int TIMEOUT = 30;

    private ApiClient() {
        client = new OkHttpClient();
        client.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    public static ApiClient getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new ApiClient();
                }
            }
        }
        return mInstance;
    }

    public void addRequestAsync(String orignal, String mRecog, Callback callback){
        RequestBody body = new FormBody.Builder()
                .add("images",(orignal + "," + mRecog))
                .build();
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(body)
                .url(MATCH_URL)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
