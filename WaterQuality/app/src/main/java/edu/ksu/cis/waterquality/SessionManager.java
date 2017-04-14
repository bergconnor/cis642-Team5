package edu.ksu.cis.waterquality;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

public class SessionManager implements AsyncResponse {

    public static final String FILENAME     = "data.json";

    public static final String KEY_ID       = "user_id";
    public static final String KEY_EMAIL    = "email";
    public static final String KEY_NAME     = "name";
    public static final String KEY_ORG      = "organization";
    public static final String KEY_DATE     = "date";
    public static final String KEY_TEST     = "test";
    public static final String KEY_SERIAL   = "serial";
    public static final String KEY_VALUE    = "concentration";
    public static final String KEY_LAT      = "latitude";
    public static final String KEY_LON      = "longitude";
    public static final String KEY_CITY     = "city";
    public static final String KEY_STATE    = "state";
    public static final String KEY_TEMP     = "temperature";
    public static final String KEY_PRECIP   = "precipitation";
    public static final String KEY_COMMENT  = "comment";

    public static final String FLAG_WEATHER  = "weather";
    public static final String FLAG_USER     = "user";

    private SharedPreferences   _pref;
    private Editor              _editor;
    private Context             _context;
    private AsyncGetUserInfo    _asyncUserTask;
    private AsyncGetWeatherData _asyncWeatherTask;
    private String[]            _keys = { KEY_ID, KEY_NAME, KEY_ORG, KEY_EMAIL, KEY_DATE, KEY_TEST,
                                          KEY_SERIAL, KEY_VALUE, KEY_LAT, KEY_LON, KEY_CITY,
                                          KEY_STATE, KEY_TEMP, KEY_PRECIP, KEY_COMMENT };

    private static final int    PRIVATE_MODE    = 0;
    private static final String KEY_PREF        = "WaterQualityPref";
    private static final String KEY_LOGIN       = "IsLoggedIn";

    public SessionManager(Context context){
        this._context       = context;
        _pref               = _context.getSharedPreferences(KEY_PREF, PRIVATE_MODE);
        _editor             = _pref.edit();
        _asyncUserTask      = new AsyncGetUserInfo();
        _asyncWeatherTask   = new AsyncGetWeatherData();
    }

    public void createLoginSession(String email){
        _editor.putBoolean(KEY_LOGIN, true);
        _editor.putString(KEY_EMAIL, email);

        _editor.commit();
    }

    public void addSessionVariables(List<String> keys, List<String> values) {
        for(int i = 0; i < keys.size(); i++) {
            _editor.putString(keys.get(i), values.get(i));
        }
        _editor.commit();
    }

    public boolean checkLogin(){
        boolean isLoggedIn = this.isLoggedIn();
        if(isLoggedIn){

        } else {
            // user is logged in, get user info
            _asyncUserTask.delegate = this;
            _asyncUserTask.email    = _pref.getString(KEY_EMAIL, null);
            _asyncWeatherTask.delegate = this;
            try {
                _asyncUserTask.execute();
            }
            catch (Exception ex) {
                // TO DO: handle error
                ex.printStackTrace();
            }
        }
        return isLoggedIn;
    }

    public LinkedHashMap<String, String> getDetails() {
        LinkedHashMap<String, String> details = new LinkedHashMap<String, String>();

//        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
//        _editor.putString(KEY_DATE, date);
//        _editor.commit();
//        details.put(KEY_DATE, date);

        for(int i = 0; i < _keys.length; i++) {
            if(_pref.contains(_keys[i]))
                details.put(_keys[i], _pref.getString(_keys[i], null));
        }

        return details;
    }

    public void updateValue(String key, String value) {
        _editor.remove(key);
        _editor.putString(key, value);
        _editor.commit();
    }

    public void logoutUser(){
        // clear all data from Shared Preferences
        _editor.clear();
        _editor.commit();
    }

    public boolean isLoggedIn(){
        return _pref.getBoolean(KEY_LOGIN, false);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) _context
                .getSystemService(_context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            return true;
        } else {
            // not connected to the internet
            return false;
        }
    }

    @Override
    public void processFinish(String result){
        try {
            JSONObject json = new JSONObject(result);
            if(json.has(FLAG_WEATHER)) {
                _editor.putString(KEY_TEMP, json.getString(KEY_TEMP));
                _editor.putString(KEY_PRECIP, json.getString(KEY_PRECIP));
            } else if(json.has(FLAG_USER)) {
                _editor.putString(KEY_ID, json.getString(KEY_ID));
                _editor.putString(KEY_NAME, json.getString(KEY_NAME));
                _editor.putString(KEY_ORG, json.getString(KEY_ORG));
            }
            _editor.commit();
        } catch(Exception ex) {
            // TO DO: handle exception
            ex.printStackTrace();
        }
    }
}