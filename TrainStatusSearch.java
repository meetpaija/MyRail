package com.example.meetpaija.myrail;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.example.meetpaija.myrail.TrainFare.TrainFareEnquiry;
import com.example.meetpaija.myrail.TrainSchedule.TrainScheduleClass;
import com.example.meetpaija.myrail.TrainSchedule.TrainScheduleSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class TrainStatusSearch extends AppCompatActivity {

    AutoCompleteTextView train_no;
    TextView date,train_name,actarr,actdep,scharr,schdpt,stationname,stationstatus,curr_pos,arrdate;
    FloatingActionButton fab;
    RelativeLayout rl2;
    Spinner station;
    private boolean isPanelShown;
    int year_x,month_x,day_x;
    static final int dialog_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_status_search);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Train Live Status");
        actionBar.setDisplayHomeAsUpEnabled(true);

        train_name=(TextView)findViewById(R.id.trainname);
        actarr=(TextView)findViewById(R.id.actualArrival);
        actdep=(TextView)findViewById(R.id.actualDept);
        scharr=(TextView)findViewById(R.id.scheduleForArraival);
        schdpt=(TextView)findViewById(R.id.scheduleForDept);
        stationname=(TextView)findViewById(R.id.station);
        stationstatus=(TextView)findViewById(R.id.statusForStation);
        curr_pos=(TextView)findViewById(R.id.currentPosition);
        arrdate=(TextView)findViewById(R.id.arrivalDate);
        isPanelShown=false;
        rl2=(RelativeLayout)findViewById(R.id.rl2);


        station=(Spinner)findViewById(R.id.station_picker);
        train_no=(AutoCompleteTextView) findViewById(R.id.train_no);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        date=(TextView)findViewById(R.id.date);
        ReusableCode reusableCode=new ReusableCode();
        final String trainDetails[]=new String[reusableCode.train.length];
        for(int i=0;i<reusableCode.train.length;i++)
        {
            String temp_details[]=reusableCode.train[i].split("\t");
            trainDetails[i]=temp_details[0].replaceAll("\\s+", "").trim()+"- "+temp_details[1];
        }
        train_no.setThreshold(2);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.select_dialog_item,trainDetails);
        train_no.setAdapter(arrayAdapter);


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

        train_no.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String traindetail[]=train_no.getText().toString().split("-");
                String train_no=traindetail[0].replaceAll("\\s+", "").trim();
                ReusableCode key = new ReusableCode();
                String APIKey = key.APIKey;

                String url = "http://api.railwayapi.com/v2/route/train/" + train_no + "/apikey/" + APIKey + "/";
                getJsonObject(url);
                return;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showstatus();
            }
        });

    }

    public void showstatus() {

       if(!valid())
           return;

        ReusableCode key = new ReusableCode();
        String APIKey = key.APIKey;

        String number []= train_no.getText().toString().trim().toLowerCase().split("-");
        String datetv=date.getText().toString().trim();

        String spliteddate[]=datetv.split("-");
        String url="http://api.railwayapi.com/v2/live/train/"+number[0]+"/date/"+spliteddate[0]+"-"+spliteddate[1]+"-"+spliteddate[2]+"/apikey/"+APIKey+"/";

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
                        String station_selected=station.getSelectedItem().toString();
                        train_name.setText("Train_Number: "+response.getString("train_number"));
                        curr_pos.setText("Current_Position: "+response.getString("position"));
                        JSONArray jsonArray=response.getJSONArray("route");
                        for (int i=0; i< jsonArray.length();i++) {
                           if(station_selected.equals(jsonArray.getJSONObject(i).getJSONObject("station_").getString("name")))
                            {
                                String has_arrived=jsonArray.getJSONObject(i).getString("has_arrived");
                                String has_dpt=jsonArray.getJSONObject(i).getString("has_departed");
                                stationstatus.setText("Station_Status :"+jsonArray.getJSONObject(i).getString("status"));
                                if(has_arrived.equals("true") && has_dpt.equals("true")) {
                                    actarr.setText("Actual_Arrived: " + jsonArray.getJSONObject(i).getString("actarr"));
                                    actdep.setText("Actual_Departured: " + jsonArray.getJSONObject(i).getString("actdep"));
                                }
                                else if(has_arrived.equals("true") && has_dpt.equals("false"))
                                {
                                    actarr.setText("Actual_Arrived: " + jsonArray.getJSONObject(i).getString("actarr"));
                                    actdep.setText("Expected_To_Departured: " + jsonArray.getJSONObject(i).getString("actdep"));
                                }
                                else if(has_arrived.equals("false") && has_dpt.equals("false"))
                                {
                                    actarr.setText("Expected_To_Arrived: " + jsonArray.getJSONObject(i).getString("actarr"));
                                    actdep.setText("Expected_To_Departured: " + jsonArray.getJSONObject(i).getString("actdep"));
                                }
                                scharr.setText("Schedule_Arrived: " + jsonArray.getJSONObject(i).getString("scharr"));
                                schdpt.setText("Schedule_Departured: " + jsonArray.getJSONObject(i).getString("schdep"));
                                stationname.setText("Station: "+jsonArray.getJSONObject(i).getJSONObject("station_").getString("name"));
                                arrdate.setText("Arraival_Date: "+jsonArray.getJSONObject(i).getString("actarr_date"));


                            }
                        }

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
                        AlertDialog.Builder aleart=new AlertDialog.Builder(TrainStatusSearch.this);
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

    private boolean valid() {
        boolean flag=true;
        station=(Spinner)findViewById(R.id.station_picker);
        train_no=(AutoCompleteTextView) findViewById(R.id.train_no);
        date=(TextView)findViewById(R.id.date);

        String number = train_no.getText().toString().trim();

        String datetv=date.getText().toString().trim();

        if(TextUtils.isEmpty(number))
        {
           train_no.setError("Please fill up first");
            flag=false;
        }
        else if(!number.contains("-"))
        {
            train_no.setError("Select the Train No/Name from the List..");
            flag=false;
        }
        else
        {
           if( station.getAdapter()==null)
           {
               Toast.makeText(getApplicationContext(),"Train number does not found",Toast.LENGTH_SHORT).show();
               flag=false;
           }
            else {
               String station_selected=station.getSelectedItem().toString();
               if (station_selected.equals("--Pick Up One Station--")) {
                   Toast.makeText(getApplicationContext(),"please select any station",Toast.LENGTH_SHORT).show();
                   flag = false;
               } else if (datetv.equals("Tap here to select the date")) {
                   Toast.makeText(getApplicationContext(),"please select the date",Toast.LENGTH_SHORT).show();
                   flag = false;
               } else {
                   train_no.setError(null);
                   flag = true;
               }
           }
        }
        return flag;
    }
    private void getJsonObject(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String code = response.getString("response_code");
                    if (code.equals("200")) {

                        JSONArray jsonArray=response.getJSONArray("route");
                        String stations[]=new String[jsonArray.length()+1];
                        stations[0]="--Pick Up One Station--";

                        for (int i=0; i< jsonArray.length();i++) {
                            String fullname=jsonArray.getJSONObject(i).getString("fullname");
                            stations[i+1]=fullname;
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                getBaseContext(), android.R.layout.simple_spinner_item, stations);
                        station.setAdapter(spinnerArrayAdapter);
                        Animation shake;
                        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                        station.setAnimation(shake);
                        return;
                    }else if(code.equals("204"))
                    {
                        Toast.makeText(getApplicationContext(),"Not able to fetch required data, Try Again Later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("510"))
                    {
                        Toast.makeText(getApplicationContext(),"Train is not scheduled to run on this day",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("404"))
                    {
                        Toast.makeText(getApplicationContext(),"Service Down / Source not responding Please Try Again Later",Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder aleart=new AlertDialog.Builder(TrainStatusSearch.this);
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
        return;
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        if(id==dialog_id)
        {
            DatePickerDialog dpd= new DatePickerDialog(this,dpickerlistner,year_x,month_x,day_x);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
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
