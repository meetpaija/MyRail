package com.example.meetpaija.myrail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.meetpaija.myrail.PNRNumber.PNRSearch;
import com.example.meetpaija.myrail.SeatAvailability.SeatAvailabilityInfo;
import com.example.meetpaija.myrail.TrainArrive.TrainArriveAtStation;
import com.example.meetpaija.myrail.TrainBetweenStationsPackage.TrainBetweenStations;
import com.example.meetpaija.myrail.TrainFare.TrainFareEnquiry;
import com.example.meetpaija.myrail.TrainSchedule.TrainScheduleSearch;

public class MainActivity extends AppCompatActivity {

    RelativeLayout schedule,pnrnum,trainstatus,trainsBetweenStations,trainArriveAtStation,seatAvailability,trainFare,otherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schedule=(RelativeLayout)findViewById(R.id.schedule);
       // pnrnum=(RelativeLayout)findViewById(R.id.pnr);
        trainstatus=(RelativeLayout)findViewById(R.id.status);
        trainsBetweenStations=(RelativeLayout)findViewById(R.id.stations);
        trainArriveAtStation=(RelativeLayout)findViewById(R.id.trainsAtStation);
        seatAvailability=(RelativeLayout)findViewById(R.id.seatAvailability);
        trainFare=(RelativeLayout)findViewById(R.id.trainFareEnquiry);
        otherInfo=(RelativeLayout)findViewById(R.id.otherInfo);

        trainFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,TrainFareEnquiry.class);
                startActivity(i);
            }
        });

        trainArriveAtStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,TrainArriveAtStation.class);
                startActivity(i);
            }
        });

        seatAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SeatAvailabilityInfo.class);
                startActivity(i);
            }
        });

        otherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,OtherInfo.class);
                startActivity(i);
            }
        });

        trainsBetweenStations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,TrainBetweenStations.class);
                startActivity(i);
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,TrainScheduleSearch.class);
                startActivity(i);
            }
        });

      /*  pnrnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,PNRSearch.class);
                startActivity(i);
            }
        });
*/
        trainstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,TrainStatusSearch.class);
                startActivity(i);
            }
        });
    }

}
