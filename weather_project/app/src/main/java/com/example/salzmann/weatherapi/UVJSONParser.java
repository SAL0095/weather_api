package com.example.salzmann.weatherapi;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UVJSONParser {

    public List<UVEntry> parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPollutionObject(reader);
        } finally {
            reader.close();
        }
    }

    public List<UVEntry> readPollutionObject(JsonReader reader) throws IOException {

        List<UVEntry> we = new ArrayList<UVEntry>();

        reader.beginArray();
        while (reader.hasNext()) {

            reader.beginObject();
            while (reader.hasNext()) {

                String name = reader.nextName();
                if (name.equals("value")) {
                    we.add(new UVEntry(reader.nextDouble()));
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        }
        reader.endArray();

        return we;
    }

}
