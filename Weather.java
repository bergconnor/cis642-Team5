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
/* Retrieve the current temperature and most recent precipitation from
 * weatherunderground.com */
public class Weather extends AsyncTask<String, String, String> {

    private static final String mTemperature   = "temperature";
    private static final String mPrecipitation = "precipitation";

    private static final int mMonthIndex = 0;
    private static final int mDayIndex   = 1;
    private static final int mYearIndex  = 2;

    private Context mContext;
    private String mLatitude;
    private String mLongitude;
    private String mCity;
    private String mState;
    /** Initiate latitude, longitude from device's GPS and save it to the corresponded vairables
     * @Parameters:
     *      Context context: current state of the application/object
     *      String latitude: the device's gps latitude
     *      String longitude: the device's gps longitude
     *      String city: the city name corresponded to the gps coordinate
     *      String state: the state of the city
     */
    public Weather(Context context, String latitude, String longitude,
                   String city, String state) {
        mContext = context;
        mLatitude = latitude;
        mLongitude = longitude;
        mCity = city;
        mState = state;
    }
    /** Get the most recent available precipitation's date
     * @Return: the most recent available precipitation's date
     */
    private String[] getYesterday() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String[] yesterday = dateFormat.format(calendar.getTime()).split("/");
        return yesterday;
    }
    /** construct a query
     * @param unit  can be either string temperature or string precipitation
     * @return      based on the string of unit, if it is temperature, a query for
     *              current location and current time temperature will be created
     *              if it is precipitation, a query for the most available precipitation
     *              will be created and returned.
     */
    private String constructTempQuery(String unit) {
        String query = "";
        String start = "http://api.wunderground.com/api/0375e5a05ebf577c/";
        String end = ".json";
        String data = "";
        if (unit.equals(mTemperature)) {
            data = "conditions/q/";
            query = start + data + mLatitude + "," + mLongitude + end;
        } else if (unit.equals(mPrecipitation)) {
            String[] yesterday = getYesterday();
            String year = yesterday[mYearIndex];
            String month = yesterday[mMonthIndex];
            String day = yesterday[mDayIndex];

            data = "history_" + year + month + day + "/q/";
            query = start + data + mState + "/" + mCity + end;
        }
        return query;
    }
    /** build connection to weather api, send query and parsing the result to
     * get either temeprate or precipitation
     * @param unit  can be either string temperature or string precipitation
     * @return      based on the string of unit, if it is temperature, the returned data
     *              will be temperature. if it is precipitation, the returned data will be
     *              precipitation.
     */
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

            if (unit.equals(mTemperature)) {
                JSONObject child = parent.getJSONObject("current_observation");
                data = child.getString("temp_f");
            } else if (unit.equals(mPrecipitation)) {
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
        String temperature = getData(mTemperature);
        String precipitation = getData(mPrecipitation);

        return temperature + ", " + precipitation;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}