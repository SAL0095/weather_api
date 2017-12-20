package com.example.salzmann.weatherapi;

import android.util.JsonReader;

import com.example.salzmann.weatherapi.WeatherEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by tonda on 12/6/2017.
 */

public class PollutionJSONParser {

    public PollutionEntry.PollutionObject parse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPollutionObject(reader);
        } finally {
            reader.close();
        }
    }

    public PollutionEntry.PollutionObject readPollutionObject(JsonReader reader) throws IOException {

        PollutionEntry.PollutionObject we = new PollutionEntry.PollutionObject();

        float val1 = 0, val2 = 0, val3 = 0;

        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("data")) {

                reader.beginArray();
                while (reader.hasNext()) {

                    reader.beginObject();
                    while (reader.hasNext()) {

                        String name2 = reader.nextName();

                        if (name2.equals("precision")) {
                            val1 = (float) reader.nextDouble();
//                            val1 = (float) 1.4;
                        } else if (name2.equals("pressure")) {
                            val2 = (float) reader.nextDouble();
//                            val2 = (float) 1.4;
                        } else if (name2.equals("value")) {
                            val3 = (float) reader.nextDouble();
//                            val3 = (float) 1.4;
                            we.data.add(new PollutionEntry.Data(val1, val2, val3));
                        }

                    }
                    reader.endObject();
                }
                reader.endArray();

            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return we;
    }
}
