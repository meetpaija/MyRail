package com.example.meetpaija.myrail.PNRNumber;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PNRSearch extends AppCompatActivity {

    EditText pnrno;
    Button searchpnr;
    RelativeLayout relativeLayout;
    TextView train_name;
    TextView chart;
    TextView trainclass;
    TextView passengers;
    TextView doj_tv;
    TextView bording;
    TextView reservation;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrsearch);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("PNR Search");
        actionBar.setDisplayHomeAsUpEnabled(true);
        pnrno=(EditText)findViewById(R.id.pnr_no);
        searchpnr=(Button)findViewById(R.id.search_pnr_btn);

        relativeLayout=(RelativeLayout)findViewById(R.id.rl2);
        relativeLayout.setVisibility(View.GONE);
        train_name=(TextView)findViewById(R.id.trainname);
        doj_tv=(TextView)findViewById(R.id.doj);
        bording=(TextView)findViewById(R.id.bording);
        reservation=(TextView)findViewById(R.id.reservation);
        passengers=(TextView)findViewById(R.id.passengers);
        trainclass=(TextView)findViewById(R.id.trainclass);
        chart=(TextView)findViewById(R.id.chart);
        tableLayout=(TableLayout)findViewById(R.id.table_main);

        searchpnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReusableCode key = new ReusableCode();
                String APIKey = key.APIKey;

                String number = pnrno.getText().toString().trim();
                String url = "http://api.railwayapi.com/pnr_status/pnr/"+number+"/apikey/"+APIKey+"/";
                getJsonObject(url);
            }
        });
    }

    private void getJsonObject(String url) {

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String failure_rate=response.getString("failure_rate");
                    String code=response.getString("response_code");
                    if(code.equals("200"))
                    {

                        String error=response.getString("error");

                        if(error.equals("false")) {
                            relativeLayout.setVisibility(View.VISIBLE);
                            tableLayout.removeAllViews();
                            ArrayList<PNRClass> PNRnumArray=new ArrayList<PNRClass>();
                            String train_name_no=(response.getString("train_name"))+(" ("+response.getString("train_num")+")");
                            String doj=response.getString("doj");
                            String chart_prepared=response.getString("chart_prepared");
                            String train_class=response.getString("class");
                            String total_passenger=response.getString("total_passengers");
                            String boarding_point=response.getJSONObject("boarding_point").getString("name");
                            String reservation_upto=response.getJSONObject("reservation_upto").getString("name");

                            JSONArray jsonArray = response.getJSONArray("passengers");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String no = jsonArray.getJSONObject(i).getString("no");
                                String booking_status = jsonArray.getJSONObject(i).getString("booking_status");
                                String current_status = jsonArray.getJSONObject(i).getString("current_status");
                                String coach_pos = jsonArray.getJSONObject(i).getString("coach_position");
                                PNRClass pnrclass = new PNRClass(no, booking_status, current_status, coach_pos);
                                PNRnumArray.add(i, pnrclass);
                            }

                            train_name.setText(train_name_no);
                            doj_tv.setText("Date Of Journy: "+doj);
                            chart.setText("Chart Prepared: "+chart_prepared);
                            passengers.setText("Total Passengers: "+total_passenger);
                            trainclass.setText("Class: "+train_class);
                            bording.setText("Bording Point: "+boarding_point);
                            reservation.setText("Reservation Upto: "+reservation_upto);

                            creatrow(PNRnumArray);

                        }
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
                        Toast.makeText(PNRSearch.this,"Service Down / Source not responding Please Try Again Later",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(code.equals("410"))
                    {
                        Toast.makeText(PNRSearch.this,"Flushed PNR / PNR not yet generated",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else if(code.equals("405"))
                    {
                        AlertDialog.Builder aleart=new AlertDialog.Builder(PNRSearch.this);
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

    private void creatrow(ArrayList<PNRClass> pnRnumArray) {
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
        tv1.setText(" Booking_Status ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER);
        tv2.setText(" Current_Status ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Coach_Position ");
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);

        for (int i = 0; i < pnRnumArray.size(); i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText(" "+pnRnumArray.get(i).no+" ");
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" "+pnRnumArray.get(i).booking_status+" ");
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextColor(Color.WHITE);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(" "+pnRnumArray.get(i).current_status+" ");
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextColor(Color.WHITE);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(" "+pnRnumArray.get(i).coach_position+" ");
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextColor(Color.WHITE);
            tbrow.addView(t4v);
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
