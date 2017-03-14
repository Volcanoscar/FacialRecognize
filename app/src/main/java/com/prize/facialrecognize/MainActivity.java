package com.prize.facialrecognize;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.baidu.aip.util.Base64Util;
import com.prize.facialrecognize.common.InternetUtil;
import com.prize.facialrecognize.common.PrizeAipFace;
import com.prize.facialrecognize.common.Utils;
import com.prize.facialrecognize.permission.PermissionManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.prize.facialrecognize.common.Constants.API_KEY;
import static com.prize.facialrecognize.common.Constants.APP_ID;
import static com.prize.facialrecognize.common.Constants.MATCH_URL;
import static com.prize.facialrecognize.common.Constants.SECRET_KEY;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Camera mCamera = null;
    private ImageView takeButton;
    private ImageView facialCheckButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    public static final String ZSD_MODE_ON = "on";
    public static final String ZSD_MODE_OFF = "off";
    private WindowManager.LayoutParams lp;
    private PermissionManager mPermissionManager;
    private int mPreviewState = K_STATE_PREVIEW;
    private static final int K_STATE_BUSY = 0;
    private static final int K_STATE_PREVIEW = 1;
    private static final int K_STATE_FROZEN = 2;

    private Utils mUtils = null;
    private PrizeAipFace mPrizeAipFace;


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_front);
        lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        mPermissionManager = new PermissionManager(this);
        if (mPermissionManager.checkCameraLaunchPermissions()) {
            bindView();
        } else {
            mPermissionManager.requestCameraLaunchPermissions();
        }
    }

    void bindView() {
        mUtils = new Utils();
        mPrizeAipFace = new PrizeAipFace(APP_ID,API_KEY,SECRET_KEY);
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        takeButton = (ImageView) findViewById(R.id.take_picture);
        facialCheckButton = (ImageView) findViewById(R.id.facial_check);
        takeButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                switch(mPreviewState) {
                    case K_STATE_FROZEN:
                        mCamera.startPreview();
                        mPreviewState = K_STATE_PREVIEW;
                        break;
                    case K_STATE_BUSY:
                        break;
                    default:
                        if (mCamera != null) {
                            takePicture();
                        }
                        mPreviewState = K_STATE_BUSY;
                        break;
                }
            }
        });

        facialCheckButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mPermissionManager.getCameraLaunchPermissionRequestCode()) {
            for (int result : grantResults) {
                if (PERMISSION_GRANTED != result) {
                    showToast(getString(R.string.permission_denied));
                    finish();
                }
            }
            bindView();
        }
    }

    @Override
    public void finish() {
        stopCamera();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        int oritationAdjust = 0;
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCamera.setDisplayOrientation(oritationAdjust);
        } catch (Exception exception) {
            showToast(getString(R.string.cameraback_fail_open));
            mCamera = null;
        }

        if (mCamera == null) {
            finish();
        } else {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                finish();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
                               int h) {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(h, w);
                parameters.setPictureSize(h, w);
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                /*
                if(ZSD_MODE_ON.equals(parameters.getZSDMode())){
					parameters.setZSDMode(ZSD_MODE_OFF);
				}
				*/
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {

        stopCamera();
    }

    private void takePicture() {

        if (mCamera != null) {
            try {
                mCamera.takePicture(mShutterCallback, rawPictureCallback,
                        jpegCallback);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        public void onShutter() {

        }
    };

    private Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {

        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {

            try {
                mCamera.stopPreview();
                mPreviewState = K_STATE_FROZEN;
                if (InternetUtil.isConnectedWifi(MainActivity.this)) {
                    final String mRecog = Base64Util.encode(_data);
                    Log.i("pengcancan",mRecog);
                    new AsyncTask<Void,Void,String>(){
                        @Override
                        protected String doInBackground(Void... params) {
                            JSONObject response = mPrizeAipFace.match(mUtils.getOrignal(MainActivity.this),mRecog);
                            return response.toString();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            Log.i("pengcancan",s);
                        }
                    }.execute();

                }
            } catch (Exception e) {

            }
        }
    };

    public final class AutoFocusCallback implements
            android.hardware.Camera.AutoFocusCallback {

        public void onAutoFocus(boolean focused, Camera camera) {

            if (focused) {
                takePicture();
            }
        }
    };

    private void stopCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
