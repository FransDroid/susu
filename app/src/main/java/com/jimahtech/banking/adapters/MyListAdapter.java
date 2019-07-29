package com.jimahtech.banking.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.ViewTransferActivity;
import com.jimahtech.banking.model.Transfer;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Transfer> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

    public MyListAdapter(ViewTransferActivity context, ArrayList<Transfer> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView codes,datee,name,acno,amount,desce,statuse;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            codes = itemView.findViewById(R.id.code);
            datee= itemView.findViewById(R.id.date);
            name= itemView.findViewById(R.id.name);
            acno= itemView.findViewById(R.id.acNumber);
            amount= itemView.findViewById(R.id.amount);
            desce= itemView.findViewById(R.id.desc);
            statuse= itemView.findViewById(R.id.status);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trans_card, viewGroup, false);
        return new ViewHolder(v, i, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.codes.setText(items.get(i).getTransCode().toUpperCase());
        viewHolder.datee.setText(items.get(i).getTransDate());
        viewHolder.name.setText(items.get(i).getRecieverName());
        viewHolder.acno.setText(items.get(i).getAccountNumber());
        viewHolder.amount.setText(String.format("GHS %s", items.get(i).getAmount()));
        viewHolder.desce.setText(items.get(i).getDesc());
        viewHolder.statuse.setText(items.get(i).getStatus());
        switch (items.get(i).getStatus()) {
            case "Processing":
                viewHolder.statuse.setTextColor(Color.BLUE);
                break;
            case "Completed":
                viewHolder.statuse.setTextColor(Color.GREEN);
                break;
            case "On Hold":
                viewHolder.statuse.setTextColor(Color.RED);
                break;
            case "Declined":
                viewHolder.statuse.setTextColor(Color.RED);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
