package edu.ksu.cis.waterquality;

import android.net.Uri;
import android.os.AsyncTask;

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

public class AsyncGetUserInfo extends AsyncTask<String, String, String>
{
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public String email = null;
    public AsyncResponse delegate = null;

    private HttpURLConnection conn;
    private URL url = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            url = new URL("http://people.cs.ksu.edu/~cberg1/android/user_info.php");

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
                    .appendQueryParameter("email", email);
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
        JSONObject json = null;
        String success      = "false";
        String message      = "";

        try {
            json = new JSONObject(result);
            json.put(SessionManager.FLAG_USER, true);
            success         = json.getString("success");
            message         = json.getString("message");
        }  catch(Exception ex) {
            // TO DO: handle error
            ex.printStackTrace();
        }

        if(success.equalsIgnoreCase("true"))
        {
            delegate.processFinish(json.toString());
        } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
            // TO DO: show toast in MainActivity
            delegate.processFinish("OOPs! Something went wrong. Connection Problem.");
        } else {
            // TO DO: show toast in MainActivity
            delegate.processFinish(message);
        }
    }
}