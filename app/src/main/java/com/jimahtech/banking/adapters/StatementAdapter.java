package com.jimahtech.banking.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.ActivityAcccountDetails;
import com.jimahtech.banking.model.Item;

import java.util.ArrayList;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView datee;
        TextView amount;
        TextView type;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            type = (TextView) itemView.findViewById(R.id.type);
            datee = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
        }
    }

    public StatementAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_card, parent, false);
        return new ViewHolder(v, viewType, context); // Returning the created object
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.type.setText(items.get(position).getType());
        if(items.get(position).getDebit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getCredit()));
            holder.amount.setTextColor(Color.parseColor("#FF2ECA71"));
        }else if(items.get(position).getCredit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getDebit()));
            holder.amount.setTextColor(Color.parseColor("#FFE54B3D"));
        }
        holder.datee.setText(items.get(position).getTransDate());
        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_right);
        holder.itemView.startAnimation(fromRight);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
