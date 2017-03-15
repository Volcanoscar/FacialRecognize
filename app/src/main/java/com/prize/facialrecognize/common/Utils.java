package com.prize.facialrecognize.common;

import android.content.Context;
import android.util.Log;

import com.prize.facialrecognize.R;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2017/3/14.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static String getOrignal(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.qwqw);
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

    private static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException t) {
            Log.w(TAG, "close fail ", t);
        }
    }

}
