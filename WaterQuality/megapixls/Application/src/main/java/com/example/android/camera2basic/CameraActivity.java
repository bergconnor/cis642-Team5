package com.example.android.camera2basic;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.graphics.SurfaceTexture;
import android.util.Size;
import java.util.ArrayList;
import java.util.Collections;
import android.util.Log;
public class CameraActivity extends Activity {
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();

        }



        CameraManager manager = (CameraManager) getSystemService(this.CAMERA_SERVICE);
        try {
            String[] cameraIdList = manager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                // Here to add camera characters the next step need:
                // https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics.html


                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                calPixls(sizes);

            }

            /*
            //Camera1.0 version
            android.hardware.Camera.Parameters params = camera.getParameters();
            List sizes = params.getSupportedPictureSizes();
            Camera.Size  result = null;

            ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
            ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();

            for (int i=0;i<sizes.size();i++){
                result = (Size) sizes.get(i);
                arrayListForWidth.add(result.width);
                arrayListForHeight.add(result.height);
                Log.debug("PictureSize", "Supported Size: " + result.width + "height : " + result.height);
                System.out.println("BACK PictureSize Supported Size: " + result.width + "height : " + result.height);
            }
            if(arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0){
                System.out.println("Back max W :"+Collections.max(arrayListForWidth));              // Gives Maximum Width
                System.out.println("Back max H :"+Collections.max(arrayListForHeight));                 // Gives Maximum Height
                System.out.println("Back Megapixel :"+( ((Collections.max(arrayListForWidth)) * (Collections.max(arrayListForHeight))) / 1024000 ) );
            }
            camera.release();

*/
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    public static double calPixls (Size[] sizes) {
        ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
        ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();

        for (Size sz:sizes) {
            arrayListForWidth.add(sz.getWidth());
            arrayListForHeight.add(sz.getHeight());
        }
        //if(arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0){
        int product = Collections.max(arrayListForWidth) * Collections.max(arrayListForHeight);
        double megapixels = product / 1024000.0;
        System.out.print(megapixels);
        return megapixels;

    }


}
