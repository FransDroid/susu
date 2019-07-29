package com.jimahtech.banking.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jimahtech.banking.R;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Statement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Statement> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView datee;
        TextView amount;
        TextView type;
        TextView name;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            type = (TextView) itemView.findViewById(R.id.type);
            datee = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    public ReceiptAdapter(Context context, ArrayList<Statement> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement2_card, parent, false);
        return new ViewHolder(v, viewType, context); // Returning the created object
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("MMMM dd, yyyy");
        toFormat.setLenient(false);
        Date date = null;
        try {
            date = fromFormat.parse(items.get(position).getTransDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.type.setText(items.get(position).getTransCode().toUpperCase());
        if(items.get(position).getDebit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getCredit()));
            holder.amount.setTextColor(Color.parseColor("#FF2ECA71"));
        }else if(items.get(position).getCredit() == 0){
            holder.amount.setText(String.format("GHS %s", items.get(position).getDebit()));
            holder.amount.setTextColor(Color.parseColor("#FFE54B3D"));
        }
        holder.datee.setText(toFormat.format(date));
        holder.name.setText(items.get(position).getName());
        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_right);
        holder.itemView.startAnimation(fromRight);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
