package com.example.salzmann.weatherapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends AppCompatActivity {

    private WeatherEntry.WeatherObject result_ = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        result_ = (WeatherEntry.WeatherObject) bundle.getSerializable("result_");
        TextView text = (TextView)findViewById(R.id.textView3);

        if (result_ != null){
            DataPoint[] points = new DataPoint[result_.listMain.size()];
            for (int i = 0; i < result_.listMain.size(); i++){
                points[i] = new DataPoint(0, result_.listMain.get(i).listMain.temp);
            }
            GraphView graph = (GraphView) findViewById(R.id.graph1);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            graph.addSeries(series);
        } else {
            text.setText("Nejprve musite stahnout data.");
        }

    }

}
