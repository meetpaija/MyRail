package com.example.meetpaija.myrail.PNRNumber;

/**
 * Created by Meet  Paija on 25-05-2017.
 */

public class PNRClass {
    String no;
    String booking_status;
    String current_status;
    String coach_position;

    public PNRClass(String no, String booking_status, String current_status, String coach_position) {
        this.no = no;
        this.booking_status = booking_status;
        this.current_status = current_status;
        this.coach_position = coach_position;
    }
}
