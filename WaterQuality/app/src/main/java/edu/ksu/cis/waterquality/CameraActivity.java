package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.SurfaceView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

/**
 * Created by Jacob on 4/11/2017.
 */

public class CameraActivity extends Activity {

    private SurfaceView overlay;
    private SurfaceView cameraView;
    private CameraManager cameraManager;

    private String getCameraId() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                //Here we get the rear facing camera(s)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                //Here we make sure that camera has stream configurations in order for us to setup a capture session.
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if(map == null) {
                    continue;
                }

                //Here we grab the large configuration
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea())
            }
        } catch(Exception e) {

        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size first, Size second) {
            return Long.signum((long) first.getWidth() * first.getHeight() - (long) second.getWidth()
            * second.getHeight());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        overlay = (SurfaceView) findViewById(R.id.overlayView);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);


    }
}
