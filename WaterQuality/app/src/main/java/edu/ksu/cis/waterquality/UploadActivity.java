package edu.ksu.cis.waterquality;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinkedHashMap<String, String> _map;
    private LinkedHashMap<String, EditText> _data = new LinkedHashMap<String, EditText>();


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // check login status
        _session = new SessionManager(getApplicationContext());
        _session.checkLogin();

        // set up progress dialog
        _dialog = new ProgressDialog(this);
        _dialog.setMessage("Uploading data to database...");

        // prompt user to enter a comment
        promptUser();
    }

    private void createView() {
        // get data and layouts
        _map = _session.getDetails();
        LinearLayout textViewLayout = (LinearLayout) findViewById(R.id.textViewLayout);
        LinearLayout editTextLayout = (LinearLayout) findViewById(R.id.editTextLayout);

        Iterator iterator = _map.entrySet().iterator();
        while(iterator.hasNext()) {
            // get hash map entry pair
            Map.Entry pair = (Map.Entry)iterator.next();

            // create new elements to add to layouts
            TextView textView = (TextView) View
                    .inflate(UploadActivity.this, R.layout.table_text_view, null);
            HorizontalScrollView scrollView = (HorizontalScrollView) View
                    .inflate(UploadActivity.this, R.layout.table_scroll_view, null);
            EditText editText = (EditText) scrollView.getChildAt(0);

            textView.setText(pair.getKey().toString());
            editText.setText(pair.getValue().toString());

            textViewLayout.addView(textView);
            editTextLayout.addView(scrollView);

            _data.put(pair.getKey().toString(), editText);
        }
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

    // Triggers when LOGIN Button clicked
    public void onUploadClick(View arg0) {
        // Initialize  AsyncLogin() class
        new AsyncUpload().execute();
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
                url = new URL("http://people.cs.ksu.edu/~cberg1/upload.php");

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
                Iterator iterator   = _map.entrySet().iterator();
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

            if(result.equalsIgnoreCase("true"))
            {
                UploadActivity.this.finish();
            } else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(UploadActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(UploadActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}