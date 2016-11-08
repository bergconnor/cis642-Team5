package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1571;
    private static final int LOCATION_REQUEST = 1995;

    private String mDate;
    private String mTest;
    private String mSerial;
    private String mTemperature;
    private String mPrecipitation;
    private String mLatitude;
    private String mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAboutButtonClicked(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onHelpButtonClicked(View v) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void onPictureButtonClicked(View v) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public void onHistoryButtonClicked(View v) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onSpreadsheetButtonClicked(View v) {
        Intent intent = new Intent(this, SpreadsheetActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST:
                    mTest = data.getStringExtra("EXTRA_TEST");
                    mSerial = data.getStringExtra("EXTRA_SERIAL");
                    Intent intent = new Intent(this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_REQUEST);
                    break;

                case LOCATION_REQUEST:
                    mLatitude = data.getStringExtra("EXTRA_LATITUDE");
                    mLongitude = data.getStringExtra("EXTRA_LONGITUDE");
                    break;
            }
        }
    }
}