package com.prize.facialrecognize.common;

import android.content.Context;
import android.util.Log;

import com.baidu.aip.util.Base64Util;
import com.prize.facialrecognize.R;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2017/3/14.
 */

public class Utils {

    private static final String TAG = "Utils";

    public String getOrignal(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.timg);
        try {
            int lenth = is.available();
            byte[] out = new byte[lenth];
            is.read(out, 0, lenth);
            return Base64Util.encode(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
        }
        return null;
    }

    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException t) {
            Log.w(TAG, "close fail ", t);
        }
    }

}
