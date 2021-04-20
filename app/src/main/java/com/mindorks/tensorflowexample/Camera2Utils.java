package com.mindorks.tensorflowexample;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.*;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Arrays;

class Camera2Utils {

/*    private static Handler mHandler;
    private static CameraDevice mCameraDevice;
    private params mState;
    private View mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ImageReader mImageReader;
    private static CaptureRequest.Builder mPreviewBuilder;
    private static CameraCaptureSession.StateCallback mSessionPreviewStateCallback;
    private static CameraCaptureSession.CaptureCallback mSessionCaptureCallback;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void initCamera2(CameraManager mCameraManager) {
        Log.d("Camera2", "Init Camera and Preview");

        mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                           TotalCaptureResult result) {
                Log.d("Camera2", "mSessionCaptureCallback CaptureCompleted");
                mSession = session;
                checkState(result);
            }

            @Override
            public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                            CaptureResult partialResult) {
                Log.d("Camera2", "mSessionCaptureCallback CaptureProgress");
                mSession = session;
                checkState(partialResult);
            }

            private void checkState(CaptureResult result) {
                switch (mState) {
                    case STATE_PREVIEW:
                        // NOTHING
                        break;
                    case STATE_WAITING_CAPTURE:
                        int afState = result.get(CaptureResult.CONTROL_AF_STATE);

                        if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                                || CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
                                || CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState) {
                            //do something like save picture
                        }
                        break;
                }
            }

        };

        mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                Log.d("Camera2", "mSessionPreviewStateCallback Configure");
                mSession = session;
                try {
                    mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    session.setRepeatingRequest(mPreviewBuilder.build(), mSessionCaptureCallback, mHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    Log.e("Camera2", "Set PreviewBuilder failed." + e.getMessage());
                }
            }
        };

        CameraDevice.StateCallback DeviceStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                Log.d("Camera2", "DeviceStateCallback:camera was opend.");
                mCameraOpenCloseLock.release();
                mCameraDevice = camera;
                try {
                    createCameraCaptureSession();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };

        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        try {
            String mCameraId = "" + CameraCharacteristics.LENS_FACING_BACK;
            mImageReader = ImageReader.newInstance(mSurfaceView.getWidth(), mSurfaceView.getHeight(),
                    ImageFormat.JPEG, 5);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);

            mCameraManager.openCamera(mCameraId, DeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            Log.e("Camera2", "open camera failed." + e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void createCameraCaptureSession() throws CameraAccessException {
        Log.d("Camera2", "createCameraCaptureSession");

        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewBuilder.addTarget(mSurfaceHolder.getSurface());
        mState = STATE_PREVIEW;
        mCameraDevice.createCaptureSession(
                Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()),
                mSessionPreviewStateCallback, mHandler);
    }*/


}
