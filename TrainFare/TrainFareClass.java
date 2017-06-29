package com.example.meetpaija.myrail.TrainFare;

/**
 * Created by Meet  Paija on 28-06-2017.
 */

public class TrainFareClass {
    String code;
    String name;
    String fare;
    String no;

    public TrainFareClass(String no,String code, String name, String fare) {
        this.code = code;
        this.no=no;
        this.name = name;
        this.fare = fare;
    }
}
