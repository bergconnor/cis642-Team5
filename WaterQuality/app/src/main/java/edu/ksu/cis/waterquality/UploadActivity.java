package edu.ksu.cis.waterquality;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class UploadActivity extends AppCompatActivity {

    private String mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    static final int DATE          = 0;
    static final int CITY          = 1;
    static final int STATE         = 2;
    static final int LATITUDE      = 3;
    static final int LONGITUDE     = 4;
    static final int TEST          = 5;
    static final int SERIAL        = 6;
    static final int TEMPERATURE   = 7;
    static final int PRECIPITATION = 8;
    static final int NAME          = 9;
    static final int ORGANIZATION  = 10;
    static final int COMMENT       = 11;
    static final int DATA_SIZE     = 12;

    private String[] mData = new String[DATA_SIZE];


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mOutputText = "";

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Uploading data to spreadsheet...");

        promptUser();
    }

    private void promptUser() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView nameLabel = new TextView(this);
        nameLabel.setText("Name:");
        layout.addView(nameLabel);

        final EditText nameBox = new EditText(this);
        nameBox.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameBox);

        final TextView orgLabel = new TextView(this);
        orgLabel.setText("Organization:");
        layout.addView(orgLabel);

        final EditText orgBox = new EditText(this);
        orgBox.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(orgBox);

        final TextView commentLabel = new TextView(this);
        commentLabel.setText("Comment (optional):");
        layout.addView(commentLabel);

        final EditText commentBox = new EditText(this);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(commentBox);


        final AlertDialog alert = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Test Information")
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = nameBox.getText().toString();
                        String organization = orgBox.getText().toString();
                        String comment = commentBox.getText().toString();
                        String message;
                        if (name.length() < 1 && organization.length() < 1) {
                            message = "Name and organization are required!";
                            Toast.makeText(UploadActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                        else if (name.length() < 1 && organization.length() > 0) {
                            message = "Name is required!";
                            Toast.makeText(UploadActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        } else if (organization.length() < 1 && name.length() > 0) {
                            message = "Organization is required!";
                            Toast.makeText(UploadActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            alert.dismiss();
                            compileData(name, organization, comment);
                        }
                    }
                });
            }
        });

        alert.show();
    }

    private EditText[] getData() {
        EditText[] editTexts = new EditText[DATA_SIZE];

        editTexts[DATE]          = (EditText) this.findViewById(R.id.dateEdit);
        editTexts[CITY]          = (EditText) this.findViewById(R.id.cityEdit);
        editTexts[STATE]         = (EditText) this.findViewById(R.id.stateEdit);
        editTexts[LATITUDE]      = (EditText) this.findViewById(R.id.latitudeEdit);
        editTexts[LONGITUDE]     = (EditText) this.findViewById(R.id.longitudeEdit);
        editTexts[TEST]          = (EditText) this.findViewById(R.id.testEdit);
        editTexts[SERIAL]        = (EditText) this.findViewById(R.id.serialEdit);
        editTexts[TEMPERATURE]   = (EditText) this.findViewById(R.id.temperatureEdit);
        editTexts[PRECIPITATION] = (EditText) this.findViewById(R.id.precipitationEdit);
        editTexts[NAME]          = (EditText) this.findViewById(R.id.nameEdit);
        editTexts[ORGANIZATION]  = (EditText) this.findViewById(R.id.organizationEdit);
        editTexts[COMMENT]       = (EditText) this.findViewById(R.id.commentEdit);

        return editTexts;
    }

    private void setText() {
        EditText[] editTexts = getData();

        for (int i = 0; i < DATA_SIZE; i++) {
            editTexts[i].setText(mData[i]);
        }
    }

    private void compileData(String name, String organization, String comment) {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        mData[DATE]          = data.getString("EXTRA_DATE");
        mData[CITY]          = data.getString("EXTRA_CITY");
        mData[STATE]         = data.getString("EXTRA_STATE");
        mData[LATITUDE]      = data.getString("EXTRA_LATITUDE");
        mData[LONGITUDE]     = data.getString("EXTRA_LONGITUDE");
        mData[TEST]          = data.getString("EXTRA_TEST");
        mData[SERIAL]        = data.getString("EXTRA_SERIAL");
        mData[TEMPERATURE]   = data.getString("EXTRA_TEMPERATURE");
        mData[PRECIPITATION] = data.getString("EXTRA_PRECIPITATION");
        mData[NAME]          = name;
        mData[ORGANIZATION]  = organization;
        mData[COMMENT]       = comment;

        setText();
    }

    private void recompileData() {
        EditText[] editTexts = getData();

        for (int i = 0; i < DATA_SIZE; i++) {
            mData[i] = editTexts[i].getText().toString();
        }
    }

    // Triggers when LOGIN Button clicked
    public void uploadClick(View arg0) {
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

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", mData[NAME])
                        .appendQueryParameter("organization", mData[ORGANIZATION])
                        .appendQueryParameter("latitude", mData[LATITUDE])
                        .appendQueryParameter("longitude", mData[LONGITUDE]);
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

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                UploadActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(UploadActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(UploadActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}