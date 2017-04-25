package edu.ksu.cis.waterquality;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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


public class UploadActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private SessionManager _session;
    private ProgressDialog _dialog;
    private FileManager _fileManager;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        _session = new SessionManager(getApplicationContext());

        // set up progress dialog
        _dialog = new ProgressDialog(this);
        _dialog.setMessage("Uploading data to database...");

        _fileManager = new FileManager(UploadActivity.this);

        // prompt user to enter a comment
        promptUser();
    }

    private void createView() {
        // get data and layouts
        LinkedHashMap<String, String> map = _session.getDetails();
        LinearLayout tableLayout = (LinearLayout) View
                .inflate(UploadActivity.this, R.layout.table, null);
        LinearLayout tableLabelsLayout = (LinearLayout) tableLayout.getChildAt(0);
        LinearLayout tableDataLayout = (LinearLayout) tableLayout.getChildAt(1);

        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            // get hash map entry pair
            Map.Entry pair      = (Map.Entry)iterator.next();
            final String key    = pair.getKey().toString();

            if(!key.equals(_session.KEY_ID)) {

                // create new elements to add to layouts
                TextView tableLabel = (TextView) View
                        .inflate(UploadActivity.this, R.layout.table_label, null);
                HorizontalScrollView tableData;

                tableLabel.setText(pair.getKey().toString());

                if(key.equals(_session.KEY_COMMENT)) {
                    tableData = (HorizontalScrollView) View
                            .inflate(UploadActivity.this, R.layout.table_data_edit, null);
                    EditText data = (EditText) tableData.getChildAt(0);

                    data.setText(pair.getValue().toString());
                    data.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable editable) {
                            _session.updateValue(key, editable.toString());
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // do nothing
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // do nothing
                        }
                    });
                } else {
                    tableData = (HorizontalScrollView) View
                            .inflate(UploadActivity.this, R.layout.table_data_view, null);
                    TextView data = (TextView) tableData.getChildAt(0);

                    data.setText(pair.getValue().toString());
                }

                tableLabelsLayout.addView(tableLabel);
                tableDataLayout.addView(tableData);
            }
        }
        ScrollView layout = (ScrollView)findViewById(R.id.uploadLayout);
        layout.addView(tableLayout);
    }

    private void promptUser() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView commentLabel = new TextView(this);
        commentLabel.setText("Enter a comment if you'd like to:");
        layout.addView(commentLabel);

        final EditText commentBox = new EditText(this);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(commentBox);


        final AlertDialog alert = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Comment")
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String comment = commentBox.getText().toString();
                        alert.dismiss();

                        // initialize lists
                        List<String> keys   = new ArrayList<String>();
                        List<String> values = new ArrayList<String>();

                        // add keys and values to lists
                        keys.add(SessionManager.KEY_COMMENT);
                        values.add(comment);

                        // add data to session variables
                        _session.addSessionVariables(keys, values);

                        // create view
                        createView();
                    }
                });
            }
        });

        alert.show();
    }

    public void onUploadClicked(View v) {
        if(_session.isConnected() && _session.isLoggedIn()) {
            // initialize  AsyncLogin() class
            new AsyncUpload().execute();
        } else {
            try {
                JSONArray markers;
                String jsonString = _fileManager.readFile(_session.FILENAME);
                if(jsonString.equals("")) {
                    markers = new JSONArray();
                } else {
                    JSONObject json = new JSONObject(_fileManager.readFile(_session.FILENAME));
                    markers = json.getJSONArray("markers");
                }
                LinkedHashMap<String, String> map = _session.getDetails();

                JSONObject marker = new JSONObject();
                Iterator iterator   = map.entrySet().iterator();
                while(iterator.hasNext()) {
                    // get hash map entry pair
                    Map.Entry pair = (Map.Entry) iterator.next();
                    marker.put(pair.getKey().toString(), pair.getValue().toString());
                }
                markers.put(marker);
                _fileManager.writeFile(_session.FILENAME, new JSONObject().put("markers", markers).toString());
                UploadActivity.this.finish();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class AsyncUpload extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UploadActivity.this);
        HttpURLConnection conn;
        URL url = null;

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
                Iterator iterator   = map.entrySet().iterator();
                while(iterator.hasNext()) {
                    // get hash map entry pair
                    Map.Entry pair = (Map.Entry) iterator.next();
                    builder.appendQueryParameter(pair.getKey().toString(), pair.getValue().toString());
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

            try {
                JSONObject reader = new JSONObject(result);
                success = reader.getString("success");
                message = reader.getString("message");

            }  catch(Exception ex) {
                // TO DO: handle error
                ex.printStackTrace();
            }

            if(success.equalsIgnoreCase("true"))
            {
                UploadActivity.this.finish();
            } else if (result.equalsIgnoreCase("false")){
                // if username and password does not match display a error message
                Toast.makeText(UploadActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(UploadActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UploadActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}