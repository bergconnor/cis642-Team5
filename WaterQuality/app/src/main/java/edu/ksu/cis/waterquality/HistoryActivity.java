package edu.ksu.cis.waterquality;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * History page to display the data from past samples taken from the phone. Each item
 * will show the type of test and date. The user will be able to select multiple items
 * and view more data about each item, or delete the item(s).
 */
public class HistoryActivity extends AppCompatActivity {

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

        buildHomepage();
    }

    /**
     * Builds/rebuilds the history page based on the date saved in the json file.
     *
     * @return void
     */
    private void buildHomepage() {
        _rows = new ArrayList<>();
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new CustomArrayAdapter(this, _rows));
        try {
            HistoryRow row;
            JSONObject json = new JSONObject(_fileManager.readFile(_session.FILENAME));
            _markers = json.getJSONArray("markers");
            for(int i = (_markers.length() - 1); i >= 0; i--) {
                JSONObject marker = _markers.getJSONObject(i);
                row = new HistoryRow();
                row.setTitle(marker.get("test").toString());
                row.setSubtitle(marker.get("date").toString());
                _rows.add(row);
            }
        } catch(Exception ex) {
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

    }

    /**
     * Removes selected items from the json file.
     *
     * @param v parent View of the button
     * @return void
     */
    public void onDeleteButtonClicked(View v) {
        for(int i = _rows.size(); i >= 0; i--) {
            if(_rows.get(i).isChecked()) {
                _markers.remove(i);
            }
        }
        try {
            _fileManager.writeFile(_session.FILENAME, new JSONObject().put("markers", _markers).toString());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        buildHomepage();
    }
}
