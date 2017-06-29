package com.example.meetpaija.myrail.TrainBetweenStationsPackage;

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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrainBetweenStations extends AppCompatActivity {

FloatingActionButton fab;
    RelativeLayout rl2,rl1;
    AutoCompleteTextView source,dest;
    TextView datepickup;
    TableLayout tableLayout;
    int year_x,month_x,day_x;
    private boolean isPanelShown;
    ProgressBar progress;
    static final int dialog_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_between_stations);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Trains Between Stations");
        actionBar.setDisplayHomeAsUpEnabled(true);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        source=(AutoCompleteTextView) findViewById(R.id.sourceinput);
        dest=(AutoCompleteTextView) findViewById(R.id.destinput);
        datepickup=(TextView)findViewById(R.id.datepickup);
        tableLayout=(TableLayout)findViewById(R.id.table_main);
        rl2=(RelativeLayout)findViewById(R.id.rl2);
        rl1=(RelativeLayout)findViewById(R.id.rl1);
        progress=(ProgressBar)findViewById(R.id.progressbar);
        isPanelShown=false;

        ReusableCode reusableCode=new ReusableCode();
        String stations[]=reusableCode.StationName;

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.select_dialog_item,stations);

        source.setThreshold(2);
        source.setAdapter(arrayAdapter);

        dest.setThreshold(2);
        dest.setAdapter(arrayAdapter);

        java.util.Calendar cal=java.util.Calendar.getInstance();

        year_x=cal.get(java.util.Calendar.YEAR);
        month_x=cal.get(java.util.Calendar.MONTH);
        day_x=cal.get(java.util.Calendar.DAY_OF_MONTH);



        datepickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dialog_id);
                return;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(!validate())
                return;


                String sourcename[]=source.getText().toString().toLowerCase().split("-");
                String destname[]=dest.getText().toString().toLowerCase().split("-");

                String sourcecode=sourcename[1].replaceAll("\\s+", "").trim();
                String destcode=destname[1].replaceAll("\\s+", "").trim();

                String spliteddate[]=datepickup.getText().toString().split("-");

                ReusableCode reusableCode1=new ReusableCode();
                String apikey=reusableCode1.APIKey;

                String url="http://api.railwayapi.com/between/source/"+sourcecode+"/dest/"+destcode+"/date/"+spliteddate[0]+"-"+spliteddate[1]+"/apikey/"+apikey;

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
                        if(TextUtils.isEmpty(response.getString("error"))) {

                            if(!isPanelShown) {
                                // Show the panel
                                Animation bottomUp = AnimationUtils.loadAnimation(TrainBetweenStations.this,
                                        R.anim.bottom_up);

                                rl2.startAnimation(bottomUp);
                                rl2.setVisibility(View.VISIBLE);
                                isPanelShown = true;
                            }

                            progress.setVisibility(View.VISIBLE);
                            tableLayout.removeAllViews();
                            ArrayList<TrainBetweenStationClass> trainsArray = new ArrayList<TrainBetweenStationClass>();

                            JSONArray jsonArray = response.getJSONArray("train");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String no = jsonArray.getJSONObject(i).getString("no");
                                String train_no = jsonArray.getJSONObject(i).getString("number");
                                String train_name = jsonArray.getJSONObject(i).getString("name");
                                String src_dept_time=jsonArray.getJSONObject(i).getString("src_departure_time");
                                String dest_arr_time=jsonArray.getJSONObject(i).getString("dest_arrival_time");
                                String to = jsonArray.getJSONObject(i).getJSONObject("to").getString("name");
                                String from = jsonArray.getJSONObject(i).getJSONObject("from").getString("name");
                                String travel_time = jsonArray.getJSONObject(i).getString("travel_time");

                                JSONArray jsonArray1=jsonArray.getJSONObject(i).getJSONArray("classes");
                                String classes="";

                                for(int j=0;j<jsonArray1.length();j++)
                                {
                                    if(jsonArray1.getJSONObject(j).getString("available").equals("Y")) {
                                        classes = classes + new String(" " + jsonArray1.getJSONObject(j).getString("class-code"));
                                    }
                                }

                                if(classes.equals(""))
                                {
                                    classes="-";
                                }

                                TrainBetweenStationClass trainBetweenStationClass=new TrainBetweenStationClass(no,train_no,train_name,src_dept_time,dest_arr_time,from,to,travel_time,classes);
                                trainsArray.add(i,trainBetweenStationClass);

                            }
                            createTableRow(trainsArray);
                            progress.setVisibility(View.GONE);
                            return;
                        }
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
                        AlertDialog.Builder aleart=new AlertDialog.Builder(TrainBetweenStations.this);
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

    private void createTableRow(ArrayList<TrainBetweenStationClass> trainsArray) {

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
        tv3.setText(" Src_Dept_Time ");
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Dest_Arr_Time ");
        tv4.setGravity(Gravity.CENTER);
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        TextView tv5 = new TextView(this);
        tv5.setText(" From_Station ");
        tv5.setGravity(Gravity.CENTER);
        tv5.setTextColor(Color.WHITE);
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" To_Station ");
        tv6.setGravity(Gravity.CENTER);
        tv6.setTextColor(Color.WHITE);
        tbrow0.addView(tv6);
        TextView tv7 = new TextView(this);
        tv7.setText(" Travel_Time ");
        tv7.setGravity(Gravity.CENTER);
        tv7.setTextColor(Color.WHITE);
        tbrow0.addView(tv7);
        TextView tv8 = new TextView(this);
        tv8.setText(" Classes ");
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
            t2v.setText(" "+trainsArray.get(i).train_number+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+trainsArray.get(i).train_name+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(" "+trainsArray.get(i).source_departure_time+" ");
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextColor(Color.WHITE);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(" "+trainsArray.get(i).dest_arrival_time+" ");
            t5v.setGravity(Gravity.CENTER);
            t5v.setTextColor(Color.WHITE);
            tbrow.addView(t5v);
            TextView t6v = new TextView(this);
            t6v.setText(" "+trainsArray.get(i).source+" ");
            t6v.setGravity(Gravity.CENTER);
            t6v.setTextColor(Color.WHITE);
            tbrow.addView(t6v);
            TextView t7v = new TextView(this);
            t7v.setText(" "+trainsArray.get(i).dest+" ");
            t7v.setGravity(Gravity.CENTER);
            t7v.setTextColor(Color.WHITE);
            tbrow.addView(t7v);
            TextView t8v = new TextView(this);
            t8v.setText(" "+trainsArray.get(i).travel_time+" ");
            t8v.setGravity(Gravity.CENTER);
            t8v.setTextColor(Color.WHITE);
            tbrow.addView(t8v);
            TextView t9v = new TextView(this);
            t9v.setText(" "+trainsArray.get(i).classes+" ");
            t9v.setGravity(Gravity.CENTER);
            t9v.setTextColor(Color.WHITE);
            tbrow.addView(t9v);
            stk.addView(tbrow);
        }
    }


    private boolean validate() {
        boolean flag=true;

        String s=source.getText().toString().toLowerCase().trim();
        String d=dest.getText().toString().toLowerCase().trim();
        String date=datepickup.getText().toString().trim();

        if(TextUtils.isEmpty(s) || TextUtils.isEmpty(d))
        {
            Toast.makeText(TrainBetweenStations.this,"Source and Destination must not be empty..",Toast.LENGTH_SHORT).show();
            flag=false;
        }
        else if(!s.contains("-") || !d.contains("-"))
        {
            Toast.makeText(TrainBetweenStations.this,"Select the Stations from the List..",Toast.LENGTH_SHORT).show();
            flag=false;
        }
        else if(s.equals(d))
        {
            Toast.makeText(TrainBetweenStations.this,"Source and Destination must be different..",Toast.LENGTH_SHORT).show();
            flag=false;
        }
        else if(date.equals("Tap here to select the Date"))
        {
            Toast.makeText(TrainBetweenStations.this,"Select the date first",Toast.LENGTH_SHORT).show();
            datepickup.setError("Select the date first");
            flag=false;
        }
        else
        {
            source.setError(null);
            dest.setError(null);
            datepickup.setError(null);
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
            setTextView(date_x);
            return;
        }
    };

    private void setTextView(String date_x) {
        datepickup.setText(date_x);
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
