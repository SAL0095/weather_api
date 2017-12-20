package com.example.salzmann.weatherapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonda on 12/6/2017.
 */

public class PollutionEntry {

    static public class Data {
        public float precision;
        public float pressure;
        public float value;
        Data(){}
        Data(float precision, float pressure, float value){
            this.precision = precision;
            this.pressure = pressure;
            this.value = value;
        }
    }

    static public class PollutionObject{
        List<Data> data;
        PollutionObject(){
            data = new ArrayList<PollutionEntry.Data>();
        }
    }

}
