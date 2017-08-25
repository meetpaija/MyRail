package com.example.meetpaija.myrail.ReScheduleTrainPackage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meetpaija.myrail.CancellTrainPackage.CancellTrain;
import com.example.meetpaija.myrail.CancellTrainPackage.CancellTrainClass;
import com.example.meetpaija.myrail.R;
import com.example.meetpaija.myrail.ReusableCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RescheduleTrains extends AppCompatActivity {
    private boolean isPanelShown;
    int year_x,month_x,day_x;
    static final int dialog_id=0;
    TextView date;
    TableLayout tableLayout;
    RelativeLayout rl2;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_trains);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Reschedule Trains");
        actionBar.setDisplayHomeAsUpEnabled(true);

        isPanelShown=false;
        rl2=(RelativeLayout)findViewById(R.id.rl2);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        date=(TextView)findViewById(R.id.date);
        tableLayout=(TableLayout)findViewById(R.id.table_main);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dialog_id);
                return;
            }
        });

        java.util.Calendar cal=java.util.Calendar.getInstance();

        year_x=cal.get(java.util.Calendar.YEAR);
        month_x=cal.get(java.util.Calendar.MONTH);
        day_x=cal.get(java.util.Calendar.DAY_OF_MONTH);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtrains();
            }
        });
    }
    private void showtrains() {
        if(!valid())
            return;

        ReusableCode key = new ReusableCode();
        String APIKey = key.APIKey;

        String datetv=date.getText().toString().trim();

        String url="http://api.railwayapi.com/v2/rescheduled/date/"+datetv+"/apikey/"+APIKey+"/";

        getJsonStatus(url);
        return;
    }
    private void getJsonStatus(String url) {
        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code=response.getString("response_code");
                    if(code.equals("200"))
                    {
                        if(!isPanelShown) {
                            // Show the panel
                            Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.bottom_up);

                            rl2.startAnimation(bottomUp);
                            rl2.setVisibility(View.VISIBLE);
                            isPanelShown = true;
                        }
                        tableLayout.removeAllViews();
                        ArrayList<RescheduleTrainClass> trainScheduleArray=new ArrayList<RescheduleTrainClass>();
                        JSONArray jsonArray=response.getJSONArray("trains");
                        for (int i=0; i< jsonArray.length();i++) {

                            String no=String.valueOf(i+1);
                            String num=jsonArray.getJSONObject(i).getString("number");
                            String name=jsonArray.getJSONObject(i).getString("name");
                            String to=jsonArray.getJSONObject(i).getJSONObject("to").getString("name")+"-"+jsonArray.getJSONObject(i).getJSONObject("to").getString("code");
                            String from=jsonArray.getJSONObject(i).getJSONObject("from").getString("name")+"-"+jsonArray.getJSONObject(i).getJSONObject("from").getString("code");
                            String redate=jsonArray.getJSONObject(i).getString("rescheduled_date");
                            String retime=jsonArray.getJSONObject(i).getString("rescheduled_time");
                            String time_diff=jsonArray.getJSONObject(i).getString("time_diff");
                            RescheduleTrainClass trainScheduleClass=new RescheduleTrainClass(no,num,name,to,from,redate,retime,time_diff);
                            trainScheduleArray.add(i,trainScheduleClass);

                        }

                        creatrow(trainScheduleArray);
                        return;

                    }
                    else if(code.equals("204"))
                    {
                        Toast.makeText(getApplicationContext(),"Not able to fetch required data, Try Again Later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("404"))
                    {
                        Toast.makeText(getApplicationContext(),"Service Down / Source not responding Please Try Again Later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("510"))
                    {
                        Toast.makeText(getApplicationContext(),"Train is not scheduled to run on this day",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("401"))
                    {
                        Toast.makeText(getApplicationContext(),"Authentication Error",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("403"))
                    {
                        Toast.makeText(getApplicationContext(),"Quota for the day exhausted, Try Again on Nextday",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("405"))
                    {
                        AlertDialog.Builder aleart=new AlertDialog.Builder(RescheduleTrains.this);
                        aleart.setTitle(Html.fromHtml("<font color='#4d587b'>Update Require..</font>"));
                        aleart.setMessage("Account Expired , Update Your Application Now...");
                        aleart.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        aleart.setNegativeButton("Cancell", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        AlertDialog alertDialog=aleart.create();
                        alertDialog.show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Some Error Occure, Try Again later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            " Check Your Connection..",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(),
                            "AuthFailureError , Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(),
                            "ServerError, Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),
                            "NetworkError, Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(),
                            "ParseError, Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Some Error Occure in Network, Try again later",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        requestQueue2.add(jsonObjectRequest2);
    }

    private void creatrow(ArrayList<RescheduleTrainClass> TrainDetails) {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.BLACK);
        TextView tv0 = new TextView(this);
        tv0.setText(" No ");
        tv0.setGravity(Gravity.CENTER);
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER);
        tv1.setText(" Number ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText(" Name ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" From ");
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" To ");
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        TextView tv5 = new TextView(this);
        tv5.setText(" Rescheduled_Date ");
        tv5.setGravity(Gravity.CENTER);
        tv5.setTextColor(Color.WHITE);
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" Rescheduled_Time ");
        tv6.setGravity(Gravity.CENTER);
        tv6.setTextColor(Color.WHITE);
        tbrow0.addView(tv6);
        TextView tv7 = new TextView(this);
        tv7.setText(" Time_Difference ");
        tv7.setGravity(Gravity.CENTER);
        tv7.setTextColor(Color.WHITE);
        tbrow0.addView(tv7);
        stk.addView(tbrow0);

        for (int i = 0; i < TrainDetails.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(" "+TrainDetails.get(i).no+" ");
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" "+TrainDetails.get(i).num+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+TrainDetails.get(i).name+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(" "+TrainDetails.get(i).from+" ");
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextColor(Color.WHITE);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(" "+TrainDetails.get(i).to+" ");
            t5v.setGravity(Gravity.CENTER);
            t5v.setTextColor(Color.WHITE);
            tbrow.addView(t5v);
            TextView t6v = new TextView(this);
            t6v.setText(" "+TrainDetails.get(i).rescheduled_date+" ");
            t6v.setGravity(Gravity.CENTER);
            t6v.setTextColor(Color.WHITE);
            tbrow.addView(t6v);
            TextView t7v = new TextView(this);
            t7v.setText(" "+TrainDetails.get(i).rescheduled_time+" ");
            t7v.setGravity(Gravity.CENTER);
            t7v.setTextColor(Color.WHITE);
            tbrow.addView(t7v);
            TextView t8v = new TextView(this);
            t8v.setText(" "+TrainDetails.get(i).time_diff+" ");
            t8v.setGravity(Gravity.CENTER);
            t8v.setTextColor(Color.WHITE);
            tbrow.addView(t8v);
            stk.addView(tbrow);
        }
    }

    private boolean valid() {
        boolean flag=true;
        date=(TextView)findViewById(R.id.date);

        String datetv=date.getText().toString().trim();

        if(datetv.equals("Tap here to select the date"))
        {
            Toast.makeText(getApplicationContext(),"please select the date",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else
        {
            flag=true;
        }
        return flag;
    }
    @Override
    protected Dialog onCreateDialog(int id) {

        if(id==dialog_id)
        {
            DatePickerDialog dpd= new DatePickerDialog(this,dpickerlistner,year_x,month_x,day_x);
            return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerlistner=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String day_str;
            String month_str;
            year_x=year;
            if(dayOfMonth<10)
                day_str='0'+String.valueOf(dayOfMonth);
            else
                day_str=String.valueOf(dayOfMonth);
            if((month+1)<10)
                month_str='0'+String.valueOf(month+1);
            else
                month_str=String.valueOf(month+1);

            String date_x=day_str+"-"+month_str+"-"+year_x;
            setEditText(date_x);
        }
    };

    private void setEditText(String date_x) {
        date.setText(date_x);
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
