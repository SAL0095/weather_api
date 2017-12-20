package com.example.salzmann.weatherapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

//implements View.OnClickListener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    private String API_KEY = "&appid=c319f89ef97e2fb480ad87c0ebaac50f";
    private static final String URL = "http://api.openweathermap.org/data/2.5/forecast?q=";

    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static boolean refreshDisplay = true;
    public static String sPref = null;
    private NetworkReceiver receiver = new NetworkReceiver();

    private Button menuButton;

    private String city, countryCode;

    private WeatherEntry.WList weather_ = null;
    public WeatherEntry.WeatherObject result_ = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buttonConfirm){
            EditText city_ = (EditText)findViewById(R.id.editTextCity);
            EditText countryCode_ = (EditText)findViewById(R.id.editTextCountry);
            city = city_.getText().toString();
            countryCode = countryCode_.getText().toString();
            if (refreshDisplay) {
                loadPage();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        menuButton = (Button)findViewById(R.id.buttonConfirm);
        menuButton.setOnClickListener(this);
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
            new DownloadJsonTask(this).execute(URL + city + "," + countryCode + "&units=metric" + API_KEY);
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

    private class DownloadJsonTask extends AsyncTask<String, Void, WeatherEntry.WeatherObject> {

        Context context;

        public DownloadJsonTask(Context context) {
            this.context = context;
        }

        @Override
        protected WeatherEntry.WeatherObject doInBackground(String... urls) {
            try {
                if (isCancelled()) return null;
                return loadJsonFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherEntry.WeatherObject result) {
            setContentView(R.layout.activity_main);

            TextView text = (TextView)findViewById(R.id.textView);
            TextView textOutput = (TextView)findViewById(R.id.textView2);

            if (result == null){
                text.setText("Data se nepodarilo nacist. Zkontrolujte sve pripojeni k internetu.");
            } else {
                text.setText("Data se uspesne nacetly..");

                result_ = result;
                weather_ = result.listMain.get(0);
                if (weather_ != null) {
                    String outputText = "Aktualni teplota: " + weather_.listMain.temp + " °C\n" +
                            "Dnesni min. teplota: " + weather_.listMain.temp_min + " °C\n" +
                            "Dnesni max. teplota: " + weather_.listMain.temp_max + " °C\n" +
                            "Tlak: " + weather_.listMain.pressure + " hPa\n" +
                            "Vlhkost: " + weather_.listMain.humidity + " %\n" +
                            "Pocasi: " + weather_.weather.main + "\n" +
                            "Popis: " + weather_.weather.description + "\n" +
                            "Oblacnost: " + weather_.clouds + " %\n" +
                            "Rychlost vetru: " + weather_.wind.speed + " m/s\n" +
                            "Posledni update: " + weather_.dt_txt;
                    textOutput.setText(outputText);

                    DataPoint[] points = new DataPoint[result_.listMain.size()];
                    DataPoint[] pointsHumidity = new DataPoint[result_.listMain.size()];
                    for (int i = 0; i < result_.listMain.size(); i++){
                        points[i] = new DataPoint(i, result_.listMain.get(i).listMain.temp);
                        pointsHumidity[i] = new DataPoint(i, result_.listMain.get(i).listMain.humidity);
                    }
                    GraphView graph = (GraphView) findViewById(R.id.graph1);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                    graph.addSeries(series);

                    GraphView graph2 = (GraphView) findViewById(R.id.graph2);
                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(pointsHumidity);
                    graph2.addSeries(series2);
                }
            }

        }
    }

    private WeatherEntry.WeatherObject loadJsonFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        WeatherEntry.WeatherObject we = null;

        WeatherJSONParser jsonParser = new WeatherJSONParser();

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
