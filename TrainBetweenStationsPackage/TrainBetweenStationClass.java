package com.example.meetpaija.myrail.TrainBetweenStationsPackage;

/**
 * Created by Meet  Paija on 13-06-2017.
 */

public class TrainBetweenStationClass {
    String no;
    String train_number;
    String train_name;
    String source_departure_time;
    String dest_arrival_time;
    String source;
    String dest;
    String travel_time;
    String classes;

    public TrainBetweenStationClass(String no,String train_number, String train_name, String source_departure_time, String dest_arrival_time, String source, String dest, String travel_time, String classes) {
        this.no=no;
        this.train_number = train_number;
        this.train_name = train_name;
        this.source_departure_time = source_departure_time;
        this.dest_arrival_time = dest_arrival_time;
        this.source = source;
        this.dest = dest;
        this.travel_time = travel_time;
        this.classes = classes;
    }
}
