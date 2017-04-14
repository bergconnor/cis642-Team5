package edu.ksu.cis.waterquality;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST      = 1571;
    private static final int LOCATION_REQUEST   = 1995;

    private SessionManager _session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check login status
        _session = new SessionManager(getApplicationContext());
        if(_session.checkLogin()) {
            Button loginButton = (Button) findViewById(R.id.loginButton);
            loginButton.setText("Logout");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST:
                    // start LocationActivity
                    intent = new Intent(this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_REQUEST);
                    break;
                case LOCATION_REQUEST:
                    // set the current date
                    List<String> keys   = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();

                    String date = new SimpleDateFormat("MM/dd/yyyy  hh:mm a" ).format(new Date());
                    keys.add(SessionManager.KEY_DATE);
                    values.add(date);

                    _session.addSessionVariables(keys, values);

                    // set city and state
                    setAddress();

                    if(_session.isConnected()) {
                        // set weather data
                        setWeatherData();
                    }

                    // start UploadActivity
                    intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    public void onAboutButtonClicked(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonClicked(View v) {
        if (_session.isConnected()) {
            if(_session.checkLogin()) {
                Button loginButton = (Button) findViewById(R.id.loginButton);
                loginButton.setText("Login");
                _session.logoutUser();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } else {
            String message = "Internet connection is required to login.";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onPictureButtonClicked(View v) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public void onMapButtonClicked(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onHistoryButtonClicked(View v) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onClearButtonClicked(View v) {
        FileManager fileManager = new FileManager(MainActivity.this);
        fileManager.clearFile(_session.FILENAME);
    }

    private void setAddress() {
        List<Address> address;
        try {
            LinkedHashMap<String, String> map = _session.getDetails();

            String latitude  = map.get(SessionManager.KEY_LAT);
            String longitude = map.get(SessionManager.KEY_LON);

            Double lat = Double.parseDouble(latitude);
            Double lon = Double.parseDouble(longitude);
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            address = gcd.getFromLocation(lat, lon, 1);
            String city = address.get(0).getLocality();
            String state = address.get(0).getAdminArea();

            // initialize lists
            List<String> keys   = new ArrayList<String>();
            List<String> values = new ArrayList<String>();

            // fill key and value lists
            keys.add(SessionManager.KEY_LAT);
            values.add(latitude);
            keys.add(SessionManager.KEY_LON);
            values.add(longitude);
            _session.addSessionVariables(keys, values);
            keys.add(SessionManager.KEY_CITY);
            values.add(city);
            keys.add(SessionManager.KEY_STATE);
            values.add(state);

            // add data to session variables
            _session.addSessionVariables(keys, values);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setWeatherData() {
        try {
            AsyncGetWeatherData asyncTask = new AsyncGetWeatherData();
            // get details from session
            HashMap<String, String> details = _session.getDetails();

            String latitude         = details.get(SessionManager.KEY_LAT);
            String longitude        = details.get(SessionManager.KEY_LON);
            String city             = details.get(SessionManager.KEY_CITY);
            String state            = details.get(SessionManager.KEY_STATE);

            asyncTask.delegate  = _session;
            asyncTask.latitude  = latitude;
            asyncTask.longitude = longitude;
            asyncTask.city      = city;
            asyncTask.state     = state;

            asyncTask.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}