package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST    = 1571;
    private static final int LOCATION_REQUEST = 1995;
    private static final int SPREADSHEET_REQUEST = 6638;
    private static final int TEMP   = 0;
    private static final int PRECIP = 1;

    private String mTest;
    private String mSerial;
    private String mColor;
    private String mLatitude;
    private String mLongitude;

    private List<LatLng> mCoordinates = new ArrayList<>();

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
        Intent intent = new Intent(this, MapsActivity.class);
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

    }

    private List<Address> getAddress() {
        List<Address> address = null;
        try {
            Double latitude = Double.parseDouble(mLatitude);
            Double longitude = Double.parseDouble(mLongitude);
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            address = gcd.getFromLocation(latitude, longitude, 1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return address;
    }

    private String[] getWeatherData(String city, String state) {
        String weather = "";
        try {
            weather = new Weather(this, mLatitude, mLongitude, city, state)
                    .execute()
                    .get();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return weather.split(", ");
    }

    private void sendData() {
        Intent intent = new Intent(this, SpreadsheetActivity.class);

        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        List<Address> address = getAddress();
        String city = address.get(0).getLocality();
        String state = address.get(0).getAdminArea();

        String[] weatherData = getWeatherData(city, state);
        String temperature = weatherData[TEMP];
        String precipitation = weatherData[PRECIP];

        intent.putExtra("EXTRA_COLOR", mColor);
        intent.putExtra("EXTRA_DATE", date);
        intent.putExtra("EXTRA_CITY", city);
        intent.putExtra("EXTRA_STATE", state);
        intent.putExtra("EXTRA_LATITUDE", mLatitude);
        intent.putExtra("EXTRA_LONGITUDE", mLongitude);
        intent.putExtra("EXTRA_TEST", mTest);
        intent.putExtra("EXTRA_SERIAL", mSerial);
        intent.putExtra("EXTRA_TEMPERATURE", temperature);
        intent.putExtra("EXTRA_PRECIPITATION", precipitation);

        //startActivity(intent);
        startActivityForResult(intent, SPREADSHEET_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST:
                    mTest = data.getStringExtra("EXTRA_TEST");
                    mSerial = data.getStringExtra("EXTRA_SERIAL");
                    mColor = data.getStringExtra("EXTRA_COLOR");
                    Intent intent = new Intent(this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_REQUEST);
                    break;

                case LOCATION_REQUEST:
                    mLatitude = data.getStringExtra("EXTRA_LATITUDE");
                    mLongitude = data.getStringExtra("EXTRA_LONGITUDE");
                    sendData();
                    break;

                case SPREADSHEET_REQUEST:
                    List<String> latitudes = data.getStringArrayListExtra("EXTRA_LATITUDES");
                    List<String> longitudes = data.getStringArrayListExtra("EXTRA_LONGITUDES");
                    for (int i = 1; i < latitudes.size(); i++)
                    {
                        double lat = Double.parseDouble(latitudes.get(i));
                        double lon = Double.parseDouble(longitudes.get(i));
                        mCoordinates.add(new LatLng(lat, lon));
                    }
                    break;
            }
        }
    }
}