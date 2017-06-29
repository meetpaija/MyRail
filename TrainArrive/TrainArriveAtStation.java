package com.example.meetpaija.myrail.TrainArrive;

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
import android.widget.ProgressBar;
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
import com.example.meetpaija.myrail.TrainBetweenStationsPackage.TrainBetweenStationClass;
import com.example.meetpaija.myrail.TrainBetweenStationsPackage.TrainBetweenStations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class TrainArriveAtStation extends AppCompatActivity {

    RelativeLayout rl2;
    AutoCompleteTextView station;
    FloatingActionButton fab;
    TextView stationname;
    ProgressBar progressBar;
    RadioRealButton button1,button2;
    RadioRealButtonGroup group;
    TableLayout tableLayout;
    boolean isPanelShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_arrive_at_station);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Trains Arrive At Station");
        actionBar.setDisplayHomeAsUpEnabled(true);

        rl2=(RelativeLayout)findViewById(R.id.rl2);
        station=(AutoCompleteTextView)findViewById(R.id.stationinput);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        tableLayout=(TableLayout)findViewById(R.id.table_main);
        stationname=(TextView)findViewById(R.id.stationname);

        ReusableCode reusableCode=new ReusableCode();
        String stations[]=reusableCode.StationName;

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.select_dialog_item,stations);

        station.setThreshold(2);
        station.setAdapter(arrayAdapter);
        isPanelShown=false;

        button1 = (RadioRealButton) findViewById(R.id.button1);
        button2 = (RadioRealButton) findViewById(R.id.button2);
        group=(RadioRealButtonGroup)findViewById(R.id.group);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validate())
                    return;

                String s[]=station.getText().toString().toLowerCase().split("-");

                String stationcode=s[1].replaceAll("\\s+", "").trim();
                Integer hour=0;

                if(button1.isChecked())
                    hour=2;
                else
                    hour=4;

                ReusableCode reusableCode1=new ReusableCode();
                String apikey=reusableCode1.APIKey;

                String url="http://api.railwayapi.com/arrivals/station/"+stationcode+"/hours/"+hour+"/apikey/"+apikey;

                getJsonResult(url);

            }
        });

    }

    private void getJsonResult(String url) {

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
                                Animation bottomUp = AnimationUtils.loadAnimation(TrainArriveAtStation.this,
                                        R.anim.bottom_up);

                                rl2.startAnimation(bottomUp);
                                rl2.setVisibility(View.VISIBLE);
                                isPanelShown = true;
                            }

                            progressBar.setVisibility(View.VISIBLE);
                            tableLayout.removeAllViews();
                            ArrayList<TrainArriveClass> trainsArray = new ArrayList<TrainArriveClass>();

                            JSONArray jsonArray = response.getJSONArray("trains");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String no = String.valueOf(i+1);
                                String train_no = jsonArray.getJSONObject(i).getString("number");
                                String train_name = jsonArray.getJSONObject(i).getString("name");
                                String scharr=jsonArray.getJSONObject(i).getString("scharr");
                                String schdep=jsonArray.getJSONObject(i).getString("schdep");
                                String actarr= jsonArray.getJSONObject(i).getString("actarr");
                                String actdep= jsonArray.getJSONObject(i).getString("actdep");
                                String delayarr = jsonArray.getJSONObject(i).getString("delayarr");
                                String delaydep = jsonArray.getJSONObject(i).getString("delaydep");

                                TrainArriveClass trainArrive=new TrainArriveClass(no,train_no,train_name,scharr,actarr,schdep,actdep,delayarr,delaydep);
                                trainsArray.add(i,trainArrive);

                            }
                        Integer hour=0;

                        if(button1.isChecked())
                            hour=2;
                        else
                            hour=4;

                        String s[]=station.getText().toString().toLowerCase().split("-");

                        String stationcode=s[1].replaceAll("\\s+", "").trim();

                            stationname.setText("These all trains will arrive within "+hour+" hours at "+stationcode+"." );

                            createTableRow(trainsArray);
                            progressBar.setVisibility(View.GONE);
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
                        AlertDialog.Builder aleart=new AlertDialog.Builder(TrainArriveAtStation.this);
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

    private void createTableRow(ArrayList<TrainArriveClass> trainsArray) {

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
        tv1.setText(" Train_Number ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText(" Train_Name ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Sch_Arr_Time ");
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Sch_Dep_Time ");
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        TextView tv5 = new TextView(this);
        tv5.setText(" Act_Arr_Time ");
        tv5.setGravity(Gravity.CENTER);
        tv5.setTextColor(Color.WHITE);
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" Act_Dep_Time ");
        tv6.setGravity(Gravity.CENTER);
        tv6.setTextColor(Color.WHITE);
        tbrow0.addView(tv6);
        TextView tv7 = new TextView(this);
        tv7.setText(" Delay_Arr_Time ");
        tv7.setGravity(Gravity.CENTER);
        tv7.setTextColor(Color.WHITE);
        tbrow0.addView(tv7);
        TextView tv8 = new TextView(this);
        tv8.setText(" Delay_Dep_Time ");
        tv8.setGravity(Gravity.CENTER);
        tv8.setTextColor(Color.WHITE);
        tbrow0.addView(tv8);
        stk.addView(tbrow0);

        for (int i = 0; i < trainsArray.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(" "+trainsArray.get(i).no+" ");
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" "+trainsArray.get(i).train_no+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+trainsArray.get(i).train_name+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(" "+trainsArray.get(i).scharr+" ");
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextColor(Color.WHITE);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(" "+trainsArray.get(i).schdep+" ");
            t5v.setGravity(Gravity.CENTER);
            t5v.setTextColor(Color.WHITE);
            tbrow.addView(t5v);
            TextView t6v = new TextView(this);
            t6v.setText(" "+trainsArray.get(i).actarr+" ");
            t6v.setGravity(Gravity.CENTER);
            t6v.setTextColor(Color.WHITE);
            tbrow.addView(t6v);
            TextView t7v = new TextView(this);
            t7v.setText(" "+trainsArray.get(i).actdep+" ");
            t7v.setGravity(Gravity.CENTER);
            t7v.setTextColor(Color.WHITE);
            tbrow.addView(t7v);
            TextView t8v = new TextView(this);
            t8v.setText(" "+trainsArray.get(i).delayarr+" ");
            t8v.setGravity(Gravity.CENTER);
            t8v.setTextColor(Color.WHITE);
            tbrow.addView(t8v);
            TextView t9v = new TextView(this);
            t9v.setText(" "+trainsArray.get(i).delaydep+" ");
            t9v.setGravity(Gravity.CENTER);
            t9v.setTextColor(Color.WHITE);
            tbrow.addView(t9v);
            stk.addView(tbrow);
        }

    }

    private boolean validate() {
        boolean flag=true;

        String s=station.getText().toString().toLowerCase().trim();

        if(TextUtils.isEmpty(s))
        {
            station.setError("Station name is required");
            flag=false;
        }
        else if(!s.contains("-"))
        {
            station.setError("Select the Station from the List..");
            flag=false;
        }
        else if(!button1.isChecked() && !button2.isChecked()) {
            Toast.makeText(TrainArriveAtStation.this, "Select any hour first..", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        else
        {
            station.setError(null);
            flag=true;
        }
        return flag;
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
