package com.prize.facialrecognize;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.prize.facialrecognize.common.ApiClient;
import com.prize.facialrecognize.common.Base64Util;
import com.prize.facialrecognize.result.ErrorResult;
import com.prize.facialrecognize.common.GsonUtils;
import com.prize.facialrecognize.common.InternetUtil;
import com.prize.facialrecognize.result.MatchResult;
import com.prize.facialrecognize.common.Utils;
import com.prize.facialrecognize.permission.PermissionManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Camera mCamera = null;
    private ImageView takeButton;
    //private ImageView facialCheckButton;
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

    private String ORIGNAL;


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
        ORIGNAL = Utils.getOrignal(this);
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        takeButton = (ImageView) findViewById(R.id.take_picture);
        //facialCheckButton = (ImageView) findViewById(R.id.facial_check);
        takeButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                switch (mPreviewState) {
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

        /*facialCheckButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

            }
        });*/
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
                    Log.i("pengcancan", "mRecog : " + mRecog);
                    Log.i("pengcancan", "ORIGNAL : " + ORIGNAL);
                    ApiClient.getInstance().addRequestAsync(ORIGNAL, mRecog, new Callback() {

                        String msg = null;

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("pengcancan", call.toString());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resultStr = response.body().string();
                            Log.i("pengcancan", resultStr);
                            MatchResult result = (MatchResult) GsonUtils.getInstance().strToBean(resultStr, MatchResult.class);
                            Log.i("pengcancan", "MatchResult : " + result.toString());
                            if (result.getResult_num() == 0) {
                                ErrorResult errorResult = (ErrorResult) GsonUtils.getInstance().strToBean(resultStr, ErrorResult.class);
                                Log.i("pengcancan", "ErrorResult : " + errorResult.toString());
                                msg = errorResult.getError_msg();
                            } else {
                                msg = MainActivity.this.getString(R.string.likelihood) + String.format("%.1f",result.getResults().get(0).getScore()) + "%";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(msg);
                                }
                            });

                        }
                    });
                }
            } catch (Exception e) {

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
