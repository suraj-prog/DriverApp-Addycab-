package com.example.uberapp.History;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberapp.HistorySingleActivity;
import com.example.uberapp.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView rideId;
    public  TextView time;
    public  TextView paid;

    public HistoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        rideId=(TextView) itemView.findViewById(R.id.rideId);
        time=(TextView) itemView.findViewById(R.id.time);
        paid=(TextView) itemView.findViewById(R.id.payment);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString( "rideId",rideId.getText().toString());
        intent.putExtras(bundle);
        v.getContext().startActivity(intent);

    }
}
