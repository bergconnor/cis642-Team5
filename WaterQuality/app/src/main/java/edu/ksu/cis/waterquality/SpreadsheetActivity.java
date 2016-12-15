package edu.ksu.cis.waterquality;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SpreadsheetActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private String mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER          = 1000;
    static final int REQUEST_AUTHORIZATION           = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES    = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    static final int COLOR          = 0;
    static final int DATE          = 1;
    static final int CITY          = 2;
    static final int STATE         = 3;
    static final int LATITUDE      = 4;
    static final int LONGITUDE     = 5;
    static final int TEST          = 6;
    static final int SERIAL        = 7;
    static final int TEMPERATURE   = 8;
    static final int PRECIPITATION = 9;
    static final int NAME          = 10;
    static final int ORGANIZATION  = 11;
    static final int COMMENT       = 12;
    static final int DATA_SIZE     = 13;

    private String[] mData = new String[DATA_SIZE];

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES          = { SheetsScopes.SPREADSHEETS };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spreadsheet);

        mOutputText = "";
        mCallApiButton = (Button) this.findViewById(R.id.uploadButton);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText = "";
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Uploading data to spreadsheet...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

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
                            Toast.makeText(SpreadsheetActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                        else if (name.length() < 1 && organization.length() > 0) {
                            message = "Name is required!";
                            Toast.makeText(SpreadsheetActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        } else if (organization.length() < 1 && name.length() > 0) {
                            message = "Organization is required!";
                            Toast.makeText(SpreadsheetActivity.this, message,
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

        editTexts[COLOR]          = (EditText) this.findViewById(R.id.colorEdit);
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

        mData[COLOR]         = data.getString("EXTRA_COLOR");
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

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText = "No network connection available.";
            Toast.makeText(SpreadsheetActivity.this, mOutputText,
                    Toast.LENGTH_LONG).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText = "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.";
                    Toast.makeText(SpreadsheetActivity.this, mOutputText,
                            Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SpreadsheetActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return uploadData();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Upload data from list to desired spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @throws IOException
         */
        private List<String> uploadData() throws IOException {
            String spreadsheetId = "17ytlyRWtIMX0z0OvPe5aI2m6CQKCDLyD2rtqpy3jwpA";
            String range = "Sheet1!A1:" + Character.toString((char)('A' + DATA_SIZE - 1));
            recompileData();

            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            int before = 0;
            List<List<Object>> values = response.getValues();
            if (values != null) {
                before = values.size();
            }

            ValueRange content = new ValueRange();
            List<List<Object>> data = new ArrayList<>();
            List<Object> row = new ArrayList<>();

            String temp;
            String entry;
            for (int i = 0; i < DATA_SIZE; i++) {
                entry = mData[i];
                if (entry.length() > 0) {
                    temp = String.format("=\"%s\"", entry);
                    row.add(temp);
                }
            }

            data.add(row);
            content.setMajorDimension("ROWS");
            content.setValues(data);

            this.mService.spreadsheets().values()
                    .append(spreadsheetId, range, content)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            List<String> results = new ArrayList<>();
            response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            int after = 0;
            values = response.getValues();
            if (values != null) {
                after = values.size();
            }

            if (after > before) {
                int count = 1;
                for (List x : values) {
                    String lat = x.get(LATITUDE).toString();
                    String lon = x.get(LONGITUDE).toString();
                    if (count == after) {
                        for (int i = 0; i < x.size(); i++) {
                            results.add(x.get(i).toString());
                        }
                    }
                    count++;
                }
            }

            return results;
        }

        @Override
        protected void onPreExecute() {
            mOutputText = "";
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText = "Failed to upload data.";
                Toast.makeText(SpreadsheetActivity.this, mOutputText,
                        Toast.LENGTH_LONG).show();
            } else {
                mOutputText = "Successfully uploaded data.";
                Toast.makeText(SpreadsheetActivity.this, mOutputText,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            SpreadsheetActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText = "The following error occurred:\n"
                            + mLastError.getMessage();
                    Toast.makeText(SpreadsheetActivity.this, mOutputText,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                mOutputText = "Request cancelled.";
                Toast.makeText(SpreadsheetActivity.this, mOutputText,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}