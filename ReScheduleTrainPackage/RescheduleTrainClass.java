package com.example.meetpaija.myrail.ReScheduleTrainPackage;

/**
 * Created by Meet  Paija on 30-06-2017.
 */

public class RescheduleTrainClass {
    String no;
    String num;
    String name;
    String to;
    String from;
    String rescheduled_date;
    String rescheduled_time;
    String time_diff;

    public RescheduleTrainClass(String no, String num, String name, String to, String from, String rescheduled_date, String rescheduled_time, String time_diff) {
        this.no = no;
        this.num = num;
        this.name = name;
        this.to = to;
        this.from = from;
        this.rescheduled_date = rescheduled_date;
        this.rescheduled_time = rescheduled_time;
        this.time_diff = time_diff;
    }
}
