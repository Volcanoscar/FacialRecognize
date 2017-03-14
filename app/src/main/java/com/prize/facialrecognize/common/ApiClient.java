package com.prize.facialrecognize.common;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.prize.facialrecognize.common.Constants.MATCH_URL;

/**
 * Created by Administrator on 2017/3/14.
 */

public class ApiClient extends Request<String> {

    private Map<String, String> mHeaders = null;
    private Map<String, String> mBody = null;

    public ApiClient(String url, String ogignal, String toCompare, Response.ErrorListener listener) {
        super(url, listener);
        mHeaders = new HashMap<>();
        mBody = new HashMap<>();
        mHeaders.put("Content-Type","application/x-www-form-urlencoded");
        mHeaders.put("accept","*/*");
        mBody.put("images",ogignal + "," + toCompare);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public int getMethod() {
        return Method.POST;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mBody;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        Log.i("pengcancan",response);
    }
}
