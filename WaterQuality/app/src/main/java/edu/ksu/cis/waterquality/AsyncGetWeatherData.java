package edu.ksu.cis.waterquality;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AsyncGetWeatherData extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;
    public String latitude = null;
    public String longitude = null;
    public String city = null;
    public String state = null;


    private static final int mMonthIndex = 0;
    private static final int mDayIndex   = 1;
    private static final int mYearIndex  = 2;

    private String[] getYesterday() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String[] yesterday = dateFormat.format(calendar.getTime()).split("/");
        return yesterday;
    }

    private String constructTempQuery(String unit) {
        String query = "";
        String start = "http://api.wunderground.com/api/0375e5a05ebf577c/";
        String end = ".json";
        String data = "";
        if (unit.equals(SessionManager.KEY_TEMP)) {
            data = "conditions/q/";
            query = start + data + latitude + "," + longitude + end;
        } else if (unit.equals(SessionManager.KEY_PRECIP)) {
            String[] yesterday = getYesterday();
            String year = yesterday[mYearIndex];
            String month = yesterday[mMonthIndex];
            String day = yesterday[mDayIndex];

            data = "history_" + year + month + day + "/q/";
            query = start + data + state + "/" + city + end;
        }
        return query;
    }

    private String getData(String unit) {
        String data = "";

        try {
            String query = constructTempQuery(unit);
            URL url = new URL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String json = buffer.toString();
            JSONObject parent = new JSONObject(json);

            if (unit.equals(SessionManager.KEY_TEMP)) {
                JSONObject child = parent.getJSONObject("current_observation");
                data = child.getString("temp_f");
            } else if (unit.equals(SessionManager.KEY_PRECIP)) {
                JSONObject child = parent.getJSONObject("history");
                JSONArray array = child.getJSONArray("dailysummary");
                JSONObject object = array.getJSONObject(0);
                data = object.getString("precipi");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    @Override
    protected String doInBackground(String... params) {
        String temperature      = getData(SessionManager.KEY_TEMP);
        String precipitation    = getData(SessionManager.KEY_PRECIP);
        JSONObject json = new JSONObject();

        try {
            json.put(SessionManager.FLAG_WEATHER, true);
            json.put(SessionManager.KEY_TEMP, temperature);
            json.put(SessionManager.KEY_PRECIP, precipitation);

        } catch(Exception ex) {
            // TO DO: handle exception
            ex.printStackTrace();
        }
        return json.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}