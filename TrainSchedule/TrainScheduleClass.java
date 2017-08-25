package com.example.meetpaija.myrail.TrainSchedule;

import java.util.ArrayList;

/**
 * Created by Meet  Paija on 24-05-2017.
 */

public class TrainScheduleClass {
    String station_code;
    String station_fullname;
    String arrival_time;
    String departure_time;
    String distance;

    public TrainScheduleClass(String station_code, String station_fullname, String arrival_time, String departure_time, String distance) {
        this.station_code = station_code;
        this.station_fullname = station_fullname;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.distance = distance;
    }


}
