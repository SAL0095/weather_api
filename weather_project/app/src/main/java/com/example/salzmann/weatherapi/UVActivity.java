package com.example.salzmann.weatherapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UVActivity extends AppCompatActivity {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    private String API_KEY = "?appid=c319f89ef97e2fb480ad87c0ebaac50f";
    // http://api.openweathermap.org/pollution/v1/co/{location}/{datetime}.json?
    private static final String URL = "http://api.openweathermap.org/data/2.5/uvi/history?";
    private String location, datetime;

    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static boolean refreshDisplay = true;
    public static String sPref = null;
    private UVActivity.NetworkReceiver receiver = new UVActivity.NetworkReceiver();
    public Button cfgButton = null;
    public EditText lat = null, lon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uv);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new UVActivity.NetworkReceiver();
        this.registerReceiver(receiver, filter);

        cfgButton = (Button)findViewById(R.id.confirmButton);
        cfgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat = (EditText)findViewById(R.id.editText2);
                lon = (EditText)findViewById(R.id.editText3);
                loadPage();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    private void loadPage() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new UVActivity.DownloadJsonTask2(this).execute(URL + "lat=" + lat.getText().toString() + "&lon=" + lon.getText().toString() + "&start=1498049953&end=1498481991" + API_KEY);
        } else {
            showErrorPage();
            updateConnectedFlags();
        }
    }

    private void showErrorPage() {
        setContentView(R.layout.activity_main);
        TextView text = (TextView)findViewById(R.id.textView);
        text.setText("Data se nepodarilo nacist. Zkontrolujte sve pripojeni k internetu.");
    }

    private class DownloadJsonTask2 extends AsyncTask<String, Void, List<UVEntry>> {

        Context context;

        public DownloadJsonTask2(Context context) {
            this.context = context;
        }

        @Override
        protected List<UVEntry> doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UVEntry> result) {
            setContentView(R.layout.activity_main);

            TextView text = (TextView)findViewById(R.id.textView4);

            if (result == null){
                text.setText("Data se nepodarilo nacist. Zkontrolujte sve pripojeni k internetu.");
            } else {
                text.setText("Data se uspesne nacetly.");

                if (result != null){
                    DataPoint[] points = new DataPoint[result.size()];
                    for (int i = 0; i < result.size(); i++){
                        points[i] = new DataPoint(i, result.get(i).value);
                    }
                    GraphView graph = (GraphView) findViewById(R.id.graph1);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                    graph.addSeries(series);
                } else {
                    text.setText("Nejprve musite stahnout data.");
                }
            }

        }
    }

    private List<UVEntry> loadJsonFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        List<UVEntry> we = null;

        UVJSONParser jsonParser = new UVJSONParser();

        stream = downloadUrl(urlString);
        we = jsonParser.parse(stream);

        return we;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                refreshDisplay = true;
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;
            } else {
                refreshDisplay = false;
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
