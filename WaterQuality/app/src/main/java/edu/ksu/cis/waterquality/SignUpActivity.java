package edu.ksu.cis.waterquality;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private EditText firstText;
    private EditText lastText;
    private EditText organizationText;
    private EditText emailText;
    private EditText password1Text;
    private EditText password2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Get Reference to variables
        firstText = (EditText) findViewById(R.id.first);
        lastText = (EditText) findViewById(R.id.last);
        emailText = (EditText) findViewById(R.id.email);
        organizationText = (EditText) findViewById(R.id.organization);
        password1Text = (EditText) findViewById(R.id.password1);
        password2Text = (EditText) findViewById(R.id.password2);
    }

    // Triggers when LOGIN Button clicked
    public void signUpClick(View arg0) {

        // Get text from fields
        final String first          = firstText.getText().toString();
        final String last           = lastText.getText().toString();
        final String organization   = organizationText.getText().toString();
        final String email          = emailText.getText().toString();
        final String password1      = password1Text.getText().toString();
        final String password2      = password2Text.getText().toString();

        // Check that the passwords are the same
        if (password1.equals(password2))
        {
            // Initialize  AsyncSignUp() class with email, username and password
            new AsyncSignUp().execute(first, last, organization, email, password1);
        }
        else {
            // If both passwords do not match display a error message
            Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncSignUp extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SignUpActivity.this);
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
                url = new URL("http://people.cs.ksu.edu/~cberg1/app/sign_up.php");

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
                        .appendQueryParameter("first", params[0])
                        .appendQueryParameter("last", params[1])
                        .appendQueryParameter("organization", params[2])
                        .appendQueryParameter("email", params[3])
                        .appendQueryParameter("password", params[4]);
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
                SignUpActivity.this.finish();
            } else if (result.equalsIgnoreCase("false")){
                // if username and password does not match display a error message
                Toast.makeText(SignUpActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(SignUpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

    }
}
