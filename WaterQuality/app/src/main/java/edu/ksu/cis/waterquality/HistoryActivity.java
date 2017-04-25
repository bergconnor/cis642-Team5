package edu.ksu.cis.waterquality;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * History page to display the data from past samples taken from the phone. Each item
 * will show the type of test and date. The user will be able to select multiple items
 * and view more data about each item, or delete the item(s).
 */
public class HistoryActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private SessionManager _session;
    private FileManager _fileManager;
    private JSONArray _markers;
    private List<HistoryRow> _rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        _session = new SessionManager(getApplicationContext());
        _fileManager = new FileManager(HistoryActivity.this);

        buildPage();
    }

    /**
     * Builds/rebuilds the history page based on the date saved in the json file.
     *
     * @return void
     */
    private void buildPage() {
        _rows = new ArrayList<>();
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new CustomArrayAdapter(this, _rows));
        try {
            HistoryRow row;
            JSONObject json = new JSONObject(_fileManager.readFile(_session.FILENAME));
            _markers = json.getJSONArray("markers");

            // loop through markers in json file and
            for (int i = (_markers.length() - 1); i >= 0; i--) {
                JSONObject marker = _markers.getJSONObject(i);
                row = new HistoryRow();
                row.setTitle(marker.get("test").toString());
                row.setSubtitle(marker.get("date").toString());
                _rows.add(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Starts an activity to display the detailed information about each selected item
     *
     * @param v parent View of the button
     * @return void
     */
    public void onViewButtonClicked(View v) {
        JSONArray selectedRows = new JSONArray();
        try {
            for (int i = (_rows.size() - 1); i >= 0; i--) {
                if (_rows.get(i).isChecked()) {
                    selectedRows.put(_markers.get(i));
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        Intent intent = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
        intent.putExtra("ROWS", selectedRows.toString());
        startActivity(intent);
    }

    /**
     * Removes selected items from the json file.
     *
     * @param v parent View of the button
     * @return void
     */
    public void onDeleteButtonClicked(View v) {
        // loop through items in list and remove if checked
        for(int i = 0; i < _rows.size(); i++) {
            if(_rows.get(i).isChecked()) {
                _markers.remove(_markers.length() - (i + 1));
            }
        }
        try {
            // rewrite
            _fileManager.writeFile(_session.FILENAME, new JSONObject().put("markers", _markers).toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        buildPage();
    }

    public void onUploadButtonClicked(View v) {
        if(_session.isConnected()) {
            // initialize  AsyncLogin() class
            if(_session.isLoggedIn()) {
                for(int i = 0; i < _rows.size(); i++) {
                    if(_rows.get(i).isChecked()) {
                        try {
                            int index = _markers.length() - (i + 1);
                            JSONObject json = _markers.getJSONObject(index);
                            AsyncUpload task = new AsyncUpload();
                            task.json = json;
                            task.index = index;
                            task.execute();
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You must be logged in to upload.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "You must be connected to the internet to upload.", Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncUpload extends AsyncTask<String, String, String>
    {
        public JSONObject json;
        public int index;

        private ProgressDialog pdLoading = new ProgressDialog(HistoryActivity.this);
        private HttpURLConnection conn;
        private URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://people.cs.ksu.edu/~cberg1/app/upload.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // build query and append variables
                Uri.Builder builder = new Uri.Builder();
                LinkedHashMap<String, String> map = _session.getDetails();
                builder.appendQueryParameter(_session.KEY_ID, map.get(_session.KEY_ID));
                builder.appendQueryParameter(_session.KEY_EMAIL, map.get(_session.KEY_EMAIL));
                builder.appendQueryParameter(_session.KEY_NAME, map.get(_session.KEY_NAME));
                builder.appendQueryParameter(_session.KEY_ORG, map.get(_session.KEY_ORG));

                Iterator<String> iterator = json.keys();
                while (iterator.hasNext()) {
                    try {
                        String key = iterator.next();
                        builder.appendQueryParameter(key, json.getString(key));
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            String success = "false";
            String message = "";
            String date ="";

            try {
                JSONObject reader = new JSONObject(result);
                success = reader.getString("success");
                message = reader.getString("message");
                date = reader.getString("date");

            }  catch(Exception ex) {
                // TO DO: handle error
                ex.printStackTrace();
            }

            if(success.equalsIgnoreCase("true"))
            {
                try {
                    _markers.remove(index);
                    _fileManager.writeFile(_session.FILENAME, new JSONObject().put("markers", _markers).toString());
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                HistoryActivity.this.finish();
            } else if (result.equalsIgnoreCase("false")){
                // if username and password does not match display a error message
                Toast.makeText(HistoryActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(HistoryActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(HistoryActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
