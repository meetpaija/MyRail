package com.example.meetpaija.myrail.TrainArrive;

/**
 * Created by Meet  Paija on 13-06-2017.
 */

public class TrainArriveClass {

    String no;
    String train_no;
    String train_name;
    String scharr;
    String actarr;
    String schdep;
    String actdep;
    String delayarr;
    String delaydep;

    public TrainArriveClass(String no, String train_no, String train_name, String scharr, String actarr, String schdep, String actdep, String delayarr, String delaydep) {
        this.no = no;
        this.train_no = train_no;
        this.train_name = train_name;
        this.scharr = scharr;
        this.actarr = actarr;
        this.schdep = schdep;
        this.actdep = actdep;
        this.delayarr = delayarr;
        this.delaydep = delaydep;
    }
}
