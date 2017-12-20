package com.example.salzmann.weatherapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        setContentView(R.layout.activity_list_view);

        lv = (ListView) findViewById(R.id.menuListView);

        List<String> levelsArrayList = new ArrayList<>();

        levelsArrayList.add("Počasí");
        levelsArrayList.add("Znečištění");
        levelsArrayList.add("UV Index");
        levelsArrayList.add("Nápověda");
        levelsArrayList.add("O aplikaci");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                levelsArrayList );

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch((int)id){
            case 0: // Weather by location
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case 1: // Pollution
                Intent intent1 = new Intent(this, PollutionActivity.class);
                startActivity(intent1);
                break;
            case 2: // UV Index
                Intent intent2 = new Intent(this, UVActivity.class);
                startActivity(intent2);
                break;
            case 3: // Help
                Intent intent3 = new Intent(this, HelpActivity.class);
                startActivity(intent3);
                break;
            case 4: // About
                Intent intent4 = new Intent(this, AboutActivity.class);
                startActivity(intent4);
                break;
            default: break;
        }

    }
    public void clearUpSharedPreferences(Context mContext)
    {
        getApplicationContext().getSharedPreferences("preferencename", 0).edit().clear().commit();
    }
}
