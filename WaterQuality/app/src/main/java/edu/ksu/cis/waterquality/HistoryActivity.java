package edu.ksu.cis.waterquality;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        try {
            JSONObject json = new JSONObject(_fileManager.readFile(_session.FILENAME));
            _markers = json.getJSONArray("markers");
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        buildHomepage();
    }

    private void buildHomepage() {
        _rows = new ArrayList<>();
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new CustomArrayAdapter(this, _rows));
        try {
            HistoryRow row;
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

    private void buildDetailsPage(JSONObject marker) {
//        _layout.removeAllViews();

        // get data and layouts
        LinearLayout tableLayout = (LinearLayout) View
                .inflate(HistoryActivity.this, R.layout.table, null);
        LinearLayout textViewLayout = (LinearLayout) tableLayout.getChildAt(0);
        LinearLayout editTextLayout = (LinearLayout) tableLayout.getChildAt(1);

        Iterator<String> iterator = marker.keys();
        while(iterator.hasNext()) {
            final String key = iterator.next();

            try {

                // create new elements to add to layouts
                TextView textView = (TextView) View
                        .inflate(HistoryActivity.this, R.layout.table_text_view, null);
                HorizontalScrollView scrollView = (HorizontalScrollView) View
                        .inflate(HistoryActivity.this, R.layout.table_scroll_view, null);
                EditText editText = (EditText) scrollView.getChildAt(0);

                textView.setText(key);
                editText.setText(marker.get(key).toString());

                editText.addTextChangedListener(new TextWatcher() {
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

                textViewLayout.addView(textView);
                editTextLayout.addView(scrollView);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        Button homeButton = new Button(HistoryActivity.this);
        homeButton.setText("History");
        homeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                buildHomepage();
            }
        });
//        _layout.addView(tableLayout);
//        _layout.addView(homeButton);
    }

    public void onViewButtonClicked(View v) {

    }

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
