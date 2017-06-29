package com.example.meetpaija.myrail.TrainSchedule;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.meetpaija.myrail.R;
import com.example.meetpaija.myrail.ReusableCode;
import com.example.meetpaija.myrail.TrainFare.TrainFareEnquiry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrainScheduleSearch extends AppCompatActivity {

    AutoCompleteTextView trainno;
    FloatingActionButton fab;
    TextView tv;
    RelativeLayout relativeLayout;
    TextView train_name;
    TextView days_runson;
    TableLayout tableLayout;
    private boolean isPanelShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedule_search);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Train Schedule");
        actionBar.setDisplayHomeAsUpEnabled(true);

        trainno=(AutoCompleteTextView) findViewById(R.id.train_no);
        fab=(FloatingActionButton) findViewById(R.id.fab);

        ReusableCode reusableCode=new ReusableCode();
        final String trainDetails[]=new String[reusableCode.train.length];
        for(int i=0;i<reusableCode.train.length;i++)
        {
            String temp_details[]=reusableCode.train[i].split("\t");
            trainDetails[i]=temp_details[0].replaceAll("\\s+", "").trim()+"- "+temp_details[1];
        }
        trainno.setThreshold(2);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.select_dialog_item,trainDetails);
        trainno.setAdapter(arrayAdapter);

        relativeLayout=(RelativeLayout)findViewById(R.id.rl2);
        relativeLayout.setVisibility(View.GONE);
        train_name=(TextView)findViewById(R.id.trainname);
        days_runson=(TextView) findViewById(R.id.trainrunson);
        tableLayout=(TableLayout)findViewById(R.id.table_main);
        isPanelShown=false;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate())
                    return;

                ReusableCode key = new ReusableCode();
                String APIKey = key.APIKey;

                String number[] = trainno.getText().toString().trim().split("-");
                String url = "http://api.railwayapi.com/route/train/" + number[0] + "/apikey/" + APIKey + "/";
                getJsonObject(url);
            }
        });
    }

    private boolean validate() {
        boolean flag=true;

        String s=trainno.getText().toString().toLowerCase().trim();
        if(TextUtils.isEmpty(s))
        {
            trainno.setError("Train No/Name is required");
            flag=false;
        }
        else if(!s.contains("-"))
        {
            trainno.setError("Select the Train No/Name from the List..");
            flag=false;
        }
        else
        {
            trainno.setError(null);
            flag=true;
        }
        return flag;
    }

    private void getJsonObject(final String url)
    {


            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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

                                relativeLayout.startAnimation(bottomUp);
                                relativeLayout.setVisibility(View.VISIBLE);
                                isPanelShown = true;
                            }
                            relativeLayout.setVisibility(View.VISIBLE);
                            tableLayout.removeAllViews();
                            ArrayList<TrainScheduleClass> trainScheduleArray=new ArrayList<TrainScheduleClass>();
                            JSONArray jsonArray=response.getJSONArray("route");
                            for (int i=0; i< jsonArray.length();i++) {

                                String station_code=jsonArray.getJSONObject(i).getString("code");
                                String fullname=jsonArray.getJSONObject(i).getString("fullname");
                                String arrivaltime=jsonArray.getJSONObject(i).getString("scharr");
                                String departuretime=jsonArray.getJSONObject(i).getString("schdep");
                                String distance=jsonArray.getJSONObject(i).getString("distance");
                                TrainScheduleClass trainScheduleClass=new TrainScheduleClass(station_code,fullname,arrivaltime,departuretime,distance);
                                trainScheduleArray.add(i,trainScheduleClass);
                            }

                            JSONArray jsonArrayRunsOn=response.getJSONObject("train").getJSONArray("days");
                            StringBuilder RunsOn=new StringBuilder("Runs On :");
                            for(int i=0;i<jsonArrayRunsOn.length();i++)
                            {

                                String runs=jsonArrayRunsOn.getJSONObject(i).getString("runs");
                                if(runs.equals("Y"))
                                {
                                    String day=jsonArrayRunsOn.getJSONObject(i).getString("day-code");
                                    RunsOn.append(" "+day);
                                }
                            }

                            String runningdays=RunsOn.toString();

                            String train_name_no=(response.getJSONObject("train").getString("name"))+(" ("+response.getJSONObject("train").getString("number")+")");

                            train_name.setText(train_name_no);
                            days_runson.setText(runningdays);
                            creatrow(trainScheduleArray);
                            return;
                        }
                        else if(code.equals("204"))
                        {
                            Toast.makeText(getApplicationContext(),"Not able to fetch required data, Try Again Later",Toast.LENGTH_SHORT).show();
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
                        else if(code.equals("404"))
                        {
                            Toast.makeText(getApplicationContext(),"Service Down / Source not responding Please Try Again Later",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(code.equals("403"))
                        {
                            Toast.makeText(getApplicationContext(),"Quota for the day exhausted, Try Again on Nextday",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(code.equals("405"))
                        {
                            AlertDialog.Builder aleart=new AlertDialog.Builder(TrainScheduleSearch.this);
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
            requestQueue.add(jsonObjectRequest);


    }

    private void creatrow(ArrayList<TrainScheduleClass> TrainDetails) {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.BLACK);
        TextView tv0 = new TextView(this);
        tv0.setText(" Station Code ");
        tv0.setGravity(Gravity.CENTER);
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER);
        tv1.setText(" Fullname ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText(" ArrivalTime ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" DepartureTime ");
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Distance ");
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        stk.addView(tbrow0);

   for (int i = 0; i < TrainDetails.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(" "+TrainDetails.get(i).station_code+" ");
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" "+TrainDetails.get(i).station_fullname+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+TrainDetails.get(i).arrival_time+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(" "+TrainDetails.get(i).departure_time+" ");
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextColor(Color.WHITE);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(" "+TrainDetails.get(i).distance+" ");
            t5v.setGravity(Gravity.CENTER);
            t5v.setTextColor(Color.WHITE);
            tbrow.addView(t5v);
            stk.addView(tbrow);
        }
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
