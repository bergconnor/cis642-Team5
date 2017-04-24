package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

/**
 * Created by Jacob on 4/11/2017.
 */

public class CameraFragment extends Fragment implements View.OnClickListener,
        FragmentCompat.OnRequestPermissionsResultCallback {

    private CameraManager manager;
    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private ImageReader mImageReader;

    private String getCameraId() {
        Activity activity = getActivity();
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                //Here we get the rear facing camera
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    //Here we grab the Stream Configurations of the camera to choose the largest
                    //configuration.
                    CameraCharacteristics camChars = manager.getCameraCharacteristics(cameraId);
                    StreamConfigurationMap map = camChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new SizeCompareArea());

                    mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 2);
                    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
                    return cameraId;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class SizeCompareArea implements Comparator<Size> {
        @Override
        public int compare(Size left, Size right) {
            return Long.signum((long) left.getWidth() * left.getHeight() - (long) right.getWidth() *
                                right.getHeight());
        }
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            texture.setDefaultBufferSize();
        }
    }
}
