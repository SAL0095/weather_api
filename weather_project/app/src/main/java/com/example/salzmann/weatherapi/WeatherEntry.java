package com.example.salzmann.weatherapi;

import java.util.List;

/**
 * Created by tonda on 11/30/2017.
 */

public class WeatherEntry {

    static public class Coord {
        public float lon, lat;
        Coord(float lon, float lat){
            this.lon = lon; this.lat = lat;
        }
        Coord(){}
    }

    static public class Weather {
        public int id;
        public String main, description, icon;
        public Weather(int id, String main, String description, String icon){
            this.id = id;
            this.main = main;
            this.description = description;
            this.icon = icon;
        }
        Weather(){}
    }

    static public class Wind {
        public float speed, deg;
        Wind(float speed, float deg){
            this.speed = speed;
            this.deg = deg;
        }
        Wind(){}
    }

    static public class ListMain {
        public float temp, temp_min, temp_max, pressure, sea_level, grnd_level, humidity, temp_kf;
        public ListMain(
                float temp,
                float temp_min,
                float temp_max,
                float pressure,
                float sea_level,
                float grnd_level,
                float humidity,
                float temp_kf
        ){
            this.temp = temp;
            this.temp_min = temp_min;
            this.temp_max = temp_max;
            this.pressure = pressure;
            this.sea_level = sea_level;
            this.grnd_level = grnd_level;
            this.humidity = humidity;
            this.temp_kf = temp_kf;
        }
        ListMain(){}
    }

    static public class WList {
        public int dt;
        public ListMain listMain;
        public Weather weather;
        public int clouds;
        public Wind wind;
        public String sys;
        public String dt_txt;
        WList(
                int dt,
                ListMain listMain,
                Weather weather,
                int clouds,
                Wind wind,
                String sys,
                String dt_txt
        ){
            this.dt = dt;
            this.listMain = listMain;
            this.weather = weather;
            this.clouds = clouds;
            this.wind = wind;
            this.sys = sys;
            this.dt_txt = dt_txt;
        }
        WList(){}
    }

    static public class City {
        public int id;
        public String name;
        public Coord coord;
        public String country;
        City(int id, String name, Coord coord, String country){
            this.id = id;
            this.name = name;
            this.coord = coord;
            this.country = country;
        }
        City(){}
    }

    static public class WeatherObject {
        City city;
        public int cod;
        public float message;
        public int cnt;
        public List<WList> listMain;
    }
}
