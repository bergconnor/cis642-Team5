package edu.ksu.cis.waterquality;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private static final String[] mTestTypes = new String[]{"Nitrate", "Phosphate"};

    private static final int PERMISSION_REQUEST = 1441;
    private static final int CAMERA_REQUEST = 1772;
    private static final int MAX_SERIAL_NUMBER = 99999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        takePicture();
    }

    private void takePicture() {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
        else {
            startCamera();
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "water_quality.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            }
            else {
                String message = "Camera use is required for this application.";
                Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
                takePicture();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "water_quality.jpg");
            String path = file.getAbsolutePath();
            Bitmap bitmap = decodeBitmapFromFile(path, 1000, 700);

            bitmap = rotateImage(bitmap, path);
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    (bitmap.getWidth()/2), bitmap.getHeight());
            String code = scanQRCode(croppedBitmap);
            double value = ImageProc.readImage(bitmap);
            processResults(code, value);
        }
    }

    private Bitmap decodeBitmapFromFile(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private Bitmap rotateImage(Bitmap bitmap, String path) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String scanQRCode(Bitmap image) {
        String contents = "";
        try {

            int[] intArray = new int[image.getWidth() * image.getHeight()];

            image.getPixels(intArray, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            LuminanceSource source = new RGBLuminanceSource(image.getWidth(), image.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            contents = result.getText();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return contents;
    }

    private void processResults(String code, double value) {
        if (code.length() > 0 && value > 0.0) {
            String message = "Value = " + value;
            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
            String[] information = code.split("\n");
            String test = information[0].split(" ")[0];
            String serial = information[1].replaceAll("[^0-9]","");
            sendResults(test, serial);
        } else {
            imageException();
//            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which){
//                        case DialogInterface.BUTTON_POSITIVE:
//                            takePicture();
//                            break;
//
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            manualEntry();
//                            break;
//                    }
//                }
//            };
//
//            final AlertDialog alert = new AlertDialog.Builder(this)
//                    .setTitle("Test Information")
//                    .setMessage("Would you like to take a new picture or manually enter the information?")
//                    .setPositiveButton("New Picture", dialogClickListener)
//                    .setNegativeButton("Manual Entry", dialogClickListener)
//                    .create();
//
//            alert.show();
        }
    }

    private void imageException() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        takePicture();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        System.exit(0);
                        break;
                }
            }
        };

        final AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to detect sample. Would you like to try again?")
                .setPositiveButton("Retake", dialogClickListener)
                .setNegativeButton("Exit", dialogClickListener)
                .create();

        alert.show();
    }

    private void manualEntry() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView testLabel = new TextView(this);
        testLabel.setText("Test type:");
        layout.addView(testLabel);

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, mTestTypes);
        spinner.setAdapter(adapter);
        layout.addView(spinner);

        final TextView serialLabel = new TextView(this);
        serialLabel.setText("Serial number:");
        layout.addView(serialLabel);

        final EditText textBox = new EditText(this);
        textBox.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(textBox);


        final AlertDialog alert = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Test Information")
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = spinner.getSelectedItem().toString();
                        String serial = textBox.getText().toString();
                        String message;
                        if (serial.length() < 1) {
                            message = "You must enter a serial number";
                            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
                        } else if(!isInteger(serial, 10)) {
                            message = "Invalid serial number";
                            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
                        } else if(Integer.parseInt(serial) > MAX_SERIAL_NUMBER ) {
                            message = "Serial numbers must be below "
                                    + String.valueOf(MAX_SERIAL_NUMBER);
                            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                        else {
                            int diff = String.valueOf(MAX_SERIAL_NUMBER).length() - serial.length();
                            for (int i = diff; i > 0; i--) {
                                serial = '0' + serial;
                            }
                            alert.dismiss();
                            sendResults(name, serial);
                        }
                    }
                });
            }
        });

        alert.show();
    }

    private boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    private void sendResults(String test, String serial) {
        Intent intent = new Intent();
        intent.putExtra("EXTRA_TEST", test);
        intent.putExtra("EXTRA_SERIAL", serial);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
