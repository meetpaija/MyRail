package com.example.meetpaija.myrail.SeatAvailability;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.example.meetpaija.myrail.PNRNumber.PNRSearch;
import com.example.meetpaija.myrail.R;
import com.example.meetpaija.myrail.ReusableCode;
import com.example.meetpaija.myrail.TrainFare.TrainFareClass;
import com.example.meetpaija.myrail.TrainFare.TrainFareEnquiry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeatAvailabilityInfo extends AppCompatActivity {

    AutoCompleteTextView trainno;
    Spinner source,dest,quota,classcode;
    TextView date,to,from,trainname,quotaname,classname;
    int year_x,month_x,day_x;
    RelativeLayout rl1,rl2;
    private boolean isPanelShown;
    static final int dialog_id=0;
    TableLayout tableLayout;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_availability);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Seat Availability");
        actionBar.setDisplayHomeAsUpEnabled(true);

        trainno=(AutoCompleteTextView)findViewById(R.id.train_no);
        source=(Spinner)findViewById(R.id.source);
        dest=(Spinner)findViewById(R.id.dest);
        quota=(Spinner)findViewById(R.id.quotacode);
        classcode=(Spinner)findViewById(R.id.classcode) ;
        date=(TextView)findViewById(R.id.date);
        to=(TextView)findViewById(R.id.to);
        from=(TextView)findViewById(R.id.from);
        trainname =(TextView)findViewById(R.id.trainname);
        quotaname=(TextView)findViewById(R.id.quotaname);
        classname=(TextView)findViewById(R.id.classname);
        isPanelShown=false;
        fab=(FloatingActionButton)findViewById(R.id.fab);
        tableLayout=(TableLayout)findViewById(R.id.table_main);
        rl2=(RelativeLayout)findViewById(R.id.rl2);

        java.util.Calendar cal=java.util.Calendar.getInstance();

        year_x=cal.get(java.util.Calendar.YEAR);
        month_x=cal.get(java.util.Calendar.MONTH);
        day_x=cal.get(java.util.Calendar.DAY_OF_MONTH);

        ReusableCode reusableCode=new ReusableCode();
        final String trainDetails[]=new String[reusableCode.train.length];
        final String quotaDetails[]=new String[reusableCode.trainquota.length+1];
        final String classDetails[]=new String[reusableCode.classcode.length+1];
        for(int i=0;i<reusableCode.train.length;i++)
        {
            String temp_details[]=reusableCode.train[i].split("\t");
            trainDetails[i]=temp_details[0].replaceAll("\\s+", "").trim()+"- "+temp_details[1];
        }
        quotaDetails[0]="--QuotaCode--";
        classDetails[0]="--ClassCode--";
        for(int i=0;i<reusableCode.trainquota.length;i++)
        {
            String quota_details[]=reusableCode.trainquota[i].split("\t");
            quotaDetails[i+1]=quota_details[0].replaceAll("\\s+", "").trim()+"- "+quota_details[1];
        }
        for(int i=0;i<reusableCode.classcode.length;i++)
        {
            classDetails[i+1]=reusableCode.classcode[i];
        }
        ArrayAdapter<String> classArrayAdapter = new ArrayAdapter<String>(
                getBaseContext(), android.R.layout.simple_spinner_item,classDetails );
        classcode.setAdapter(classArrayAdapter);
        ArrayAdapter<String> quotaArrayAdapter = new ArrayAdapter<String>(
                getBaseContext(), android.R.layout.simple_spinner_item,quotaDetails );
        quota.setAdapter(quotaArrayAdapter);

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.select_dialog_item,trainDetails);

        trainno.setThreshold(2);
        trainno.setAdapter(arrayAdapter);

        trainno.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String traindetail[]=trainno.getText().toString().split("-");
                String train_no=traindetail[0].replaceAll("\\s+", "").trim();
                source.requestFocus();
                dest.requestFocus();
                ReusableCode key = new ReusableCode();
                String APIKey = key.APIKey;

                String url = "http://api.railwayapi.com/route/train/" + train_no + "/apikey/" + APIKey + "/";
                getStations(url);
                return;
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
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

                String num[]=trainno.getText().toString().toLowerCase().trim().split("-");
                String src[]=source.getSelectedItem().toString().toLowerCase().split("-");
                String dst[]=dest.getSelectedItem().toString().toLowerCase().split("-");
                String journydate=date.getText().toString().trim();
                String qut[]=quota.getSelectedItem().toString().split("-");
                String cls[]=classcode.getSelectedItem().toString().split("-");
                ReusableCode reusableCode1=new ReusableCode();
                String apikey=reusableCode1.APIKey;

                String url=" http://api.railwayapi.com/check_seat/train/"+num[0]+"/source/"+src[1]+"/dest/"+dst[1]+"/date/"+journydate+"/class/"+cls[1]+"/quota/"+qut[0]+"/apikey/"+apikey;

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
                            Animation bottomUp = AnimationUtils.loadAnimation(SeatAvailabilityInfo.this,
                                    R.anim.bottom_up);

                            rl2.startAnimation(bottomUp);
                            rl2.setVisibility(View.VISIBLE);
                            isPanelShown = true;
                        }

                        tableLayout.removeAllViews();
                        ArrayList<SeatAvailabilityClass> trainsArray = new ArrayList<SeatAvailabilityClass>();

                        JSONArray jsonArray = response.getJSONArray("availability");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            String no = String.valueOf(i+1);
                            String quotacode = jsonArray.getJSONObject(i).getString("date");
                            String name = jsonArray.getJSONObject(i).getString("status");
                            SeatAvailabilityClass trainArrive=new SeatAvailabilityClass(no,quotacode,name);
                            trainsArray.add(i,trainArrive);
                        }

                        trainname.setText(response.getJSONObject("train").getString("number")+" - "+response.getJSONObject("train").getString("name"));
                        to.setText("To: "+response.getJSONObject("to").getString("code"));
                        from.setText("From: "+response.getJSONObject("from").getString("code"));
                        quotaname.setText("Quota: "+response.getJSONObject("quota").getString("code"));
                        classname.setText("Class: "+response.getJSONObject("travel_class").getString("code"));
                        createTableRow(trainsArray);

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
                    else if(code.equals("403"))
                    {
                        Toast.makeText(getApplicationContext(),"Quota for the day exhausted, Try Again on Nextday",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("404"))
                    {
                        Toast.makeText(getApplicationContext(),"Service Down / Source not responding Please Try Again Later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("405"))
                    {
                        AlertDialog.Builder aleart=new AlertDialog.Builder(getApplicationContext());
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

    private void createTableRow(ArrayList<SeatAvailabilityClass> trainsArray) {
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
        tv1.setText(" Date ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText(" Status ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        stk.addView(tbrow0);

        for (int i = 0; i < trainsArray.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(" "+trainsArray.get(i).no+" ");
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" "+trainsArray.get(i).date+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+trainsArray.get(i).status+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            stk.addView(tbrow);
        }

    }

    private boolean validate() {
        boolean flag=true;

        String s=trainno.getText().toString().toLowerCase().trim();
        String cls=classcode.getSelectedItem().toString();
        String qut=quota.getSelectedItem().toString();
        String datepicker=date.getText().toString().trim();

        if(TextUtils.isEmpty(s))
        {
            trainno.setError("Station name is required");
            flag=false;
        }
        else if(!s.contains("-"))
        {
            trainno.setError("Select the Station from the List..");
            flag=false;
        }
        else if(cls.equals("--ClassCode--"))
        {
            Toast.makeText(getApplicationContext(),"please select class..",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if(qut.equals("--QuotaCode--"))
        {
            Toast.makeText(getApplicationContext(),"please select quota..",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        else if(datepicker.equals("Tap here to select the date"))
        {
            Toast.makeText(getApplicationContext(),"Select the date first",Toast.LENGTH_SHORT).show();
            date.setError("Select the date first");
            flag=false;
        }
        else
        {
            if( source.getAdapter()==null || dest.getAdapter()==null)
            {
                Toast.makeText(getApplicationContext(),"Train number does not found",Toast.LENGTH_SHORT).show();
                flag=false;
            }
            else {
                String src=source.getSelectedItem().toString();
                String dst=dest.getSelectedItem().toString();

                if (src.equals("--Source--")) {
                    Toast.makeText(getApplicationContext(),"please select source station",Toast.LENGTH_SHORT).show();
                    flag = false;
                } else if (dst.equals("--Destination--")) {
                    Toast.makeText(getApplicationContext(),"please select destination station",Toast.LENGTH_SHORT).show();
                    flag = false;
                } else if(src.equals(dst)) {
                    Toast.makeText(getApplicationContext(),"Source and Destination must be different..",Toast.LENGTH_SHORT).show();
                    flag=false;
                }
                else
                {
                    trainno.setError(null);
                    date.setError(null);
                    flag=true;
                }
            }
        }
        return flag;
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if(id==dialog_id)
        {
            DatePickerDialog dpd= new DatePickerDialog(this,dpickerlistner,year_x,month_x,day_x);
            dpd.getDatePicker().setMinDate(System.currentTimeMillis());
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
        date.setText(date_x);
    }

    private void getStations(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String code = response.getString("response_code");
                    if (code.equals("200")) {

                        JSONArray jsonArray=response.getJSONArray("route");
                        String stations_1[]=new String[jsonArray.length()+1];
                        stations_1[0]="--Source--";
                        String stations_2[]=new String[jsonArray.length()+1];
                        stations_2[0]="--Destination--";

                        for (int i=0; i< jsonArray.length();i++) {
                            String fullname=jsonArray.getJSONObject(i).getString("fullname");
                            String stationcode=jsonArray.getJSONObject(i).getString("code");
                            stations_1[i+1]=fullname+"-"+stationcode;
                            stations_2[i+1]=fullname+"-"+stationcode;
                        }

                        ArrayAdapter<String> sourceArrayAdapter = new ArrayAdapter<String>(
                                getBaseContext(), android.R.layout.simple_spinner_item, stations_1);
                        source.setAdapter(sourceArrayAdapter);
                        Animation shake;
                        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                        source.setAnimation(shake);
                        ArrayAdapter<String> destArrayAdapter = new ArrayAdapter<String>(
                                getBaseContext(), android.R.layout.simple_spinner_item, stations_2);
                        dest.setAdapter(destArrayAdapter);
                        dest.setAnimation(shake);
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
                        AlertDialog.Builder aleart=new AlertDialog.Builder(getApplicationContext());
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

    private void addStation(String[] stations_1,Spinner spinner) {
        if(spinner.equals((Spinner)source))
        {
            ArrayAdapter<String> sourceArrayAdapter = new ArrayAdapter<String>(
                    getBaseContext(), android.R.layout.simple_spinner_item, stations_1);
            source.setAdapter(sourceArrayAdapter);
            Animation shake;
            shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            source.setAnimation(shake);}
        else if(spinner.equals((Spinner)dest))
        {
            stations_1[0]="--Destination--";
            ArrayAdapter<String> destArrayAdapter = new ArrayAdapter<String>(
                    getBaseContext(), android.R.layout.simple_spinner_item, stations_1);
            dest.setAdapter(destArrayAdapter);
            Animation shake;
            shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            dest.setAnimation(shake);
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
