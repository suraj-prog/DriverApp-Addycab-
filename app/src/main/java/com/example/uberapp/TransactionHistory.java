package com.example.uberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.uberapp.History.HistoryAdapter;
import com.example.uberapp.History.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransactionHistory extends AppCompatActivity {

    private String PasssengerOrDriver,UserId;
    private RecyclerView mHistory;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;

    String rideId;
    TextView textView;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        mHistory=findViewById(R.id.transactionlist);
        mHistory.setNestedScrollingEnabled(true);
        mHistory.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(TransactionHistory.this);
        mHistory.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(),TransactionHistory.this);
        mHistory.setAdapter(mHistoryAdapter);


        PasssengerOrDriver=getIntent().getExtras().getString("PassengerOrDriver");
        UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
       // getUserHistory();
        rideId=getIntent().getExtras().getString("rideId");
        textView=findViewById(R.id.textview);


       getTransaction();

    }

    private void getTransaction() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("history").child(rideId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String rideId = dataSnapshot.getKey();
                    String ridePrice = "";
                    for (DataSnapshot child :dataSnapshot.getChildren()) {

                        if (dataSnapshot.child("PassengerPaid")!=null)
                        {
                            ridePrice = dataSnapshot.child("PassengerPaid").getValue().toString();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private ArrayList historyList = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return historyList;
    }
}
