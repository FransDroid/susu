package com.jimahtech.banking.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

/**
 * Created by Chief on 2017-10-29.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

     class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView transCode;
        TextView amount;
        TextView type;
        TextView status;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            id = (TextView) itemView.findViewById(R.id.transID);
            type = (TextView) itemView.findViewById(R.id.account_type);
            transCode = (TextView) itemView.findViewById(R.id.account_number);
            amount = (TextView) itemView.findViewById(R.id.total_amount);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }

    public AccountAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_card, parent, false);
        return new ViewHolder(v, viewType, context); // Returning the created object
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.transCode.setText(items.get(position).getTransCode());
        holder.type.setText(items.get(position).getType());
        if(items.get(position).getDebit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getCredit()));
            holder.amount.setTextColor(Color.parseColor("#FF2ECA71"));
        }else if(items.get(position).getCredit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getDebit()));
            holder.amount.setTextColor(Color.parseColor("#FFE54B3D"));
        }
        holder.status.setText(items.get(position).getStatus());
        switch (items.get(position).getStatus()) {
            case "Processing":
                holder.status.setTextColor(Color.BLUE);
                break;
            case "Completed":
                holder.status.setTextColor(Color.GREEN);
                break;
            case "On Hold":
                holder.status.setTextColor(Color.RED);
                break;
            case "Declined":
                holder.status.setTextColor(Color.RED);
                break;
        }
        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_right);
        holder.itemView.startAnimation(fromRight);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = items.get(position).getDesc()+"("+items.get(position).getTransCode()+")";
                Intent intent = new Intent(context, ActivityAcccountDetails.class);
                intent.putExtra("Title", title.toUpperCase());
                intent.putExtra("Code", items.get(position).getTransCode());
                intent.putExtra("Date", items.get(position).getTransDate());
                intent.putExtra("Type", items.get(position).getType());
                intent.putExtra("Credit", items.get(position).getCredit());
                intent.putExtra("Debit", items.get(position).getDebit());
                intent.putExtra("Desc", items.get(position).getDesc());
                intent.putExtra("Status", items.get(position).getStatus());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
