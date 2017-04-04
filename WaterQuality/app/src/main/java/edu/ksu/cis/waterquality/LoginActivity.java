package edu.ksu.cis.waterquality;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT  = 10000;
    public static final int READ_TIMEOUT        = 15000;

    private SessionManager  _session;
    private EditText        _emailText;
    private EditText        _passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set session manager
        _session = new SessionManager(LoginActivity.this);

        // get reference to variables
        _emailText       = (EditText) findViewById(R.id.email);
        _passwordText    = (EditText) findViewById(R.id.password);
    }

    public void loginClick(View arg0) {

        // Get text from email and password field
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.trim().length() > 0 && password.trim().length() > 0) {
            new AsyncLogin().execute(email, password);
        } else {
            Toast.makeText(LoginActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
        }
    }

    public void signUpClick(View arg0) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        private String              _email;
        private String              _password;
        private ProgressDialog      _dialog = new ProgressDialog(LoginActivity.this);
        private HttpURLConnection   _conn;
        private URL                 _url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // initialize variables
            _email      = null;
            _password   = null;

            // set up dialog
            _dialog.setMessage("\tLoading...");
            _dialog.setCancelable(false);
            _dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            _email = params[0];
            _password = params[1];
            try {
                _url = new URL("http://people.cs.ksu.edu/~cberg1/app/login.php");
            } catch (MalformedURLException ex) {
                // TO DO: handle exception
                ex.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                _conn = (HttpURLConnection)_url.openConnection();
                _conn.setReadTimeout(READ_TIMEOUT);
                _conn.setConnectTimeout(CONNECTION_TIMEOUT);
                _conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                _conn.setDoInput(true);
                _conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", _email)
                        .appendQueryParameter("pass", _password);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = _conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                _conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = _conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = _conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // pass data to onPostExecute method
                    return(result.toString());

                } else{
                    return("unsuccessful");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return "exception";
            } finally {
                _conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            _dialog.dismiss();
            String success = null;
            String message = null;

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
                _session.createLoginSession(_email);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(LoginActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}