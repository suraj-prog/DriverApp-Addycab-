package com.example.uberapp.History;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uberapp.R;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolders>
{

    private List<HistoryObject>  itemList;
    private Context context;

    public HistoryAdapter(List<HistoryObject> itemList,Context context)
    {
        this.itemList = itemList;
        this.context=context;
    }

    @NonNull
    @Override
    public HistoryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        HistoryViewHolders historyView= new HistoryViewHolders(view);
        return historyView;

    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolders holder, int position) {
        holder.rideId.setText(itemList.get(position).getRideId());
        holder.paid.setText(itemList.get(position).getPaid());

       /* if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }*/
        holder.time.setText(itemList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
