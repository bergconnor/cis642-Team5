package edu.ksu.cis.waterquality;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.List;

/**
 * History page to display the data from past samples taken from the phone. Each item
 * will show the type of test and date. The user will be able to select multiple items
 * and view more data about each item, or delete the item(s).
 */
public class HistoryDetailsActivity extends AppCompatActivity {

    private SessionManager _session;
    private FileManager _fileManager;
    private JSONArray _markers;
    private List<HistoryRow> _rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        _session = new SessionManager(getApplicationContext());
        _fileManager = new FileManager(HistoryDetailsActivity.this);

        try {
            String json = getIntent().getStringExtra("ROWS");
            buildPage(new JSONArray(json));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildPage(JSONArray rows) {
          LinearLayout rootLayout = (LinearLayout) findViewById(R.id.linearLayout);

        for(int i = 0; i < rows.length(); i++) {
            // set up header
            TextView header = new TextView(this);
            header.setTextSize(25);
            header.setTextColor(Color.BLACK);
            header.setTypeface(null, Typeface.BOLD);
            header.setGravity(Gravity.CENTER);
            header.setBackgroundResource(R.drawable.border);

            // get data and layouts
            LinearLayout tableLayout = (LinearLayout) View
                    .inflate(HistoryDetailsActivity.this, R.layout.table, null);
            LinearLayout tableLabelsLayout = (LinearLayout) tableLayout.getChildAt(0);
            LinearLayout tableDataLayout = (LinearLayout) tableLayout.getChildAt(1);

            JSONObject marker = null;
            try {
                marker = rows.getJSONObject(i);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Iterator<String> iterator = marker.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next();

                try {
                    if(key.equals(_session.KEY_DATE)) {
                        header.setText(marker.get(key).toString());
                    } else {
                        // create new elements to add to layouts
                        TextView tableLabel = (TextView) View
                                .inflate(HistoryDetailsActivity.this, R.layout.table_label, null);
                        HorizontalScrollView tableData;

                        tableLabel.setText(key);

                        if(key.equals(_session.KEY_COMMENT)) {
                            tableData = (HorizontalScrollView) View
                                    .inflate(HistoryDetailsActivity.this, R.layout.table_data_edit, null);
                            EditText data = (EditText) tableData.getChildAt(0);

                            data.setText(marker.get(key).toString());
                        } else {
                            tableData = (HorizontalScrollView) View
                                    .inflate(HistoryDetailsActivity.this, R.layout.table_data_view, null);
                            TextView data = (TextView) tableData.getChildAt(0);

                            data.setText(marker.get(key).toString());
                        }

                        tableLabelsLayout.addView(tableLabel);
                        tableDataLayout.addView(tableData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 35));

            rootLayout.addView(header);
            rootLayout.addView(tableLayout);
            rootLayout.addView(space);
        }
    }
}
