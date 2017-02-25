package edu.ksu.cis.waterquality;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import org.opencv.core.Scalar;

import java.util.List;


public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        processImage(bitmap);
    }

    private void processImage(Bitmap bitmap) {
        List<Scalar> colors = ImageProc.readImage(bitmap);
        try {
            LineChart chart = ImageProc.createGraph(colors, getApplicationContext());
            chart = (LineChart)findViewById(R.id.chart1);
            //chart.saveToGallery("chart.jpg", 75);
        } catch (Exception e) {
            Toast.makeText(ChartActivity.this, "Unable to create chart.", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
