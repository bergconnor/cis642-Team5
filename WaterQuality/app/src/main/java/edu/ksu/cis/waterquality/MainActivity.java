package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1571;
    private static final int LOCATION_REQUEST = 1995;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAboutButtonClicked(View v) {

    }

    public void onHelpButtonClicked(View v) {

    }

    public void onPictureButtonClicked(View v) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public void onHistoryButtonClicked(View v) {

    }

    public void onSpreadsheetButtonClicked(View v) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST:
                    String test = data.getStringExtra("TEST");
                    String serial = data.getStringExtra("SERIAL");
                    break;
                case LOCATION_REQUEST:
                    break;
            }
        }
    }
}