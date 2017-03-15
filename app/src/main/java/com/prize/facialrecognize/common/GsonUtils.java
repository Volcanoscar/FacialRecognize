package com.prize.facialrecognize.common;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/3/15.
 */

public class GsonUtils<T> {

    private static GsonUtils mInstance = null;
    private static Gson mGson = null;
    private final static Object mLock = new Object();
    private GsonUtils(){
        mGson = new Gson();
    }

    public static GsonUtils getInstance() {
        if (mInstance == null){
            synchronized (mLock){
                if (mInstance == null){
                    mInstance = new GsonUtils();
                }
            }
        }
        return mInstance;
    }

    public T strToBean(String result, Class<T> clazz){
        return mGson.fromJson(result,clazz);
    }
}
