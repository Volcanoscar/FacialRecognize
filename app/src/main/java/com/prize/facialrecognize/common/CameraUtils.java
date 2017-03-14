package com.prize.facialrecognize.common;

import android.hardware.Camera;

/**
 * Created by Administrator on 2017/3/14.
 */

public class CameraUtils {

    private Camera mCamera;

    private static CameraUtils mInstance;

    private CameraUtils(){}

    public static CameraUtils getInstance() {
        if (mInstance == null){
            synchronized (mInstance){
                if (mInstance == null) {
                    mInstance = new CameraUtils();
                }
            }
        }
        return mInstance;
    }

    public boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qOpened;
    }

    public void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
