package com.example.uberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uberapp.History.HistoryAdapter;
import com.example.uberapp.History.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private TextView mBalance;
    private Double  Balance = 0.0;
    private String PasssengerOrDriver,UserId;
    private RecyclerView mHistory;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mHistory=findViewById(R.id.historyrecyclerview);
        mBalance=findViewById(R.id.balance);
        mHistory.setNestedScrollingEnabled(true);
        mHistory.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        mHistory.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(),HistoryActivity.this);
        mHistory.setAdapter(mHistoryAdapter);


        PasssengerOrDriver=getIntent().getExtras().getString("PassengerOrDriver");
        UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserHistory();
        Toast.makeText(this,"Recycler view",Toast.LENGTH_SHORT).show();

        if (PasssengerOrDriver.equals("Driver"))
        {
            mBalance.setVisibility(View.VISIBLE);
        }

    }

    private void getUserHistory()
    {
        DatabaseReference userHistoryData = FirebaseDatabase.getInstance().getReference().child("Users").child(PasssengerOrDriver).child(UserId).child("history");
        userHistoryData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {

                    for (DataSnapshot history : dataSnapshot.getChildren())
                    {
                        FetchrideInfo(history.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchrideInfo(String ridekey) {
        DatabaseReference HistoryDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(ridekey);
        HistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String rideId = dataSnapshot.getKey();
                    Long timestamp=0L;
                    Double ridepay = 0.0;
                    String distance = "";
                    String ridePrice = "";
                    String b1;

                        if (dataSnapshot.child("timestamp")!=null)
                        {
                            timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                        }
                        if (dataSnapshot.child("PassengerPaid")!=null)
                        {
                            ridePrice = dataSnapshot.child("PassengerPaid").getValue().toString();
                        }

                        if (dataSnapshot.child("PassengerPaid").getValue()!=null && dataSnapshot.child("driverPaidOut")==null)
                        {
                            if (dataSnapshot.child("distance").getValue() !=null)
                            {
                                distance = dataSnapshot.child("distance").getValue().toString();
                                System.out.println("dist"+distance);
                                ridepay =(Double.valueOf(distance)*0.4);
                                System.out.println("rideprice"+ridepay);
                                Balance += ridepay;
                                b1 = String.valueOf(Balance);
                                System.out.println("balance1"+b1);
                                mBalance.setText("Balance:" + b1 + "  Rs");
                            }
                        }


                    HistoryObject object = new HistoryObject(getDate(timestamp),ridePrice,rideId);
                    historyList.add(object);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long timestamp) {
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp*1000);

        String date = DateFormat.format("MM-dd-yyyy:hh:mm",calendar).toString();
        return date;
    }


    private ArrayList historyList = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return historyList;
    }
}
