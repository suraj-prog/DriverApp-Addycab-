package com.example.uberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mCtx;
    private List<Event> eventList;
    public EventAdapter(Context mCtx, List<Event>eventList )
    {
        this.mCtx = mCtx;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.event_list, null);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event product = eventList.get(position);
        //holder.textViewTitle.setText(product.getEventid());
        holder.textViewShortDesc.setText(product.getEventname());
        holder.textViewRating.setText(String.valueOf(product.getEventdate()));
        holder.textViewPrice.setText(String.valueOf(product.getNoofpeople()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
         //   textViewTitle = itemView.findViewById(R.id.textViewid);
            textViewShortDesc = itemView.findViewById(R.id.textViewname);
            textViewRating = itemView.findViewById(R.id.textViewdate);
            textViewPrice = itemView.findViewById(R.id.textViewpeople);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}