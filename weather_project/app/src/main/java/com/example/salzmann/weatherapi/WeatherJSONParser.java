package com.example.salzmann.weatherapi;

import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonda on 11/30/2017.
 */

public class WeatherJSONParser {

    public WeatherEntry.WeatherObject parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readWeatherObject(reader);
        } finally {
            reader.close();
        }
    }

    public WeatherEntry.WeatherObject readWeatherObject(JsonReader reader) throws IOException{

        WeatherEntry.WeatherObject we = new WeatherEntry.WeatherObject();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("cod")) {
                we.cod = reader.nextInt();
            } else if (name.equals("message")) {
                we.message = (float)reader.nextDouble();
            } else if (name.equals("ctn")) {
                we.cnt = reader.nextInt();
            } else if (name.equals("city")) {
                we.city = readCity(reader);
            } else if (name.equals("list")) {
                we.listMain = new ArrayList<WeatherEntry.WList>();
                we.listMain = readWList(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return we;
    }

    public WeatherEntry.City readCity(JsonReader reader) throws IOException{

        WeatherEntry.City city = new WeatherEntry.City();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("id")) {
                city.id = reader.nextInt();
            } else if (name.equals("name")) {
                city.name = reader.nextString();
            } else if (name.equals("coord")) {
                city.coord = readCoord(reader);
            } else if (name.equals("country")) {
                city.country = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return city;
    }

    public WeatherEntry.Coord readCoord(JsonReader reader) throws IOException{

        WeatherEntry.Coord coord = new WeatherEntry.Coord();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("lat")) {
                coord.lat = (float)reader.nextDouble();
            } else if (name.equals("lon")) {
                coord.lon = (float)reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return coord;
    }

    public List<WeatherEntry.WList> readWList(JsonReader reader) throws IOException{

        List<WeatherEntry.WList> wlist = new ArrayList<WeatherEntry.WList>();
        WeatherEntry.WList tmpWlist = null;
        boolean firstTime = false;

        reader.beginArray();
        while(reader.hasNext()){

            reader.beginObject();
            while(reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("dt")){
                    if (!firstTime){
                        firstTime = true;
                    } else {
                        wlist.add(tmpWlist);
                    }
                    tmpWlist = new WeatherEntry.WList();
                    tmpWlist.dt = reader.nextInt();
                } else if (name.equals("main")) {
                    tmpWlist.listMain = readListMain(reader);
                } else if (name.equals("weather")) {
                    tmpWlist.weather = readWeather(reader);
                } else if (name.equals("clouds")) {
                    reader.beginObject();
                    while(reader.hasNext()){
                        String namexx = reader.nextName();
                        if (namexx.equals("all")){
                            tmpWlist.clouds = reader.nextInt();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else if (name.equals("wind")) {
                    tmpWlist.wind = readWind(reader);
                } else if (name.equals("dt_txt")) {
                    tmpWlist.dt_txt = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        }
        reader.endArray();
        return wlist;
    }

    public WeatherEntry.ListMain readListMain(JsonReader reader) throws IOException{

        WeatherEntry.ListMain listMain = new WeatherEntry.ListMain();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("temp")) {
                listMain.temp = (float)reader.nextDouble();
            } else if (name.equals("temp_min")) {
                listMain.temp_min = (float)reader.nextDouble();
            } else if (name.equals("temp_max")) {
                listMain.temp_max = (float)reader.nextDouble();
            } else if (name.equals("pressure")) {
                listMain.pressure = (float)reader.nextDouble();
            } else if (name.equals("sea_level")) {
                listMain.sea_level = (float)reader.nextDouble();
            } else if (name.equals("grnd_level")) {
                listMain.grnd_level = (float)reader.nextDouble();
            } else if (name.equals("humidity")) {
                listMain.humidity = (float)reader.nextDouble();
            } else if (name.equals("temp_kf")) {
                listMain.temp_kf = (float)reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return listMain;
    }

    public WeatherEntry.Weather readWeather(JsonReader reader) throws IOException{

        WeatherEntry.Weather weather = new WeatherEntry.Weather();

        reader.beginArray();
        while(reader.hasNext()){

            reader.beginObject();
            while(reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("id")) {
                    weather.id = reader.nextInt();
                } else if (name.equals("main")) {
                    weather.main = reader.nextString();
                } else if (name.equals("description")) {
                    weather.description = reader.nextString();
                } else if (name.equals("icon")) {
                    weather.icon = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();

        return weather;
    }

    public WeatherEntry.Wind readWind(JsonReader reader) throws IOException{

        WeatherEntry.Wind wind = new WeatherEntry.Wind();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("speed")) {
                wind.speed = (float)reader.nextDouble();
            } else if (name.equals("deg")) {
                wind.deg = (float)reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return wind;
    }

}
