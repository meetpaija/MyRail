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

    public String getTrain_name_number() {
        return train_name_number;
    }

    public void setTrain_name_number(String train_name_number) {
        this.train_name_number = train_name_number;
    }

    public String getRunson() {
        return runson;
    }

    public void setRunson(String runson) {
        this.runson = runson;
    }

    public ArrayList<TrainScheduleClass> getTraindetails() {
        return traindetails;
    }

    public void setTraindetails(ArrayList<TrainScheduleClass> traindetails) {
        this.traindetails = traindetails;
    }

    String train_name_number;
    String runson;
    ArrayList<TrainScheduleClass> traindetails;

    public TrainScheduleClass(){}

    public TrainScheduleClass(String station_code, String station_fullname, String arrival_time, String departure_time, String distance) {
        this.station_code = station_code;
        this.station_fullname = station_fullname;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.distance = distance;
    }



    public String getStation_code() {
        return station_code;
    }

    public void setStation_code(String station_code) {
        this.station_code = station_code;
    }

    public String getStation_fullname() {
        return station_fullname;
    }

    public void setStation_fullname(String station_fullname) {
        this.station_fullname = station_fullname;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
