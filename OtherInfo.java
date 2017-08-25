package com.example.meetpaija.myrail;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.meetpaija.myrail.CancellTrainPackage.CancellTrain;
import com.example.meetpaija.myrail.ReScheduleTrainPackage.RescheduleTrains;

public class OtherInfo extends AppCompatActivity {
    RelativeLayout Cancell;
    RelativeLayout Reschedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_info);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Other Information");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cancell=(RelativeLayout)findViewById(R.id.canceltrain);
        Reschedule=(RelativeLayout)findViewById(R.id.rescheduletrain);
        Cancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(OtherInfo.this,CancellTrain.class);
                startActivity(i);
            }
        });
        Reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(OtherInfo.this,RescheduleTrains.class);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.right_slide_out);
                return true;
        }
        return true;
    }
}
