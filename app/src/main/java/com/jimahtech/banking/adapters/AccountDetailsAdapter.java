package com.jimahtech.banking.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jimahtech.banking.R;
import com.jimahtech.banking.model.Item;

import java.util.ArrayList;

/**
 * Created by Chief on 2017-11-02.
 */

public class AccountDetailsAdapter extends RecyclerView.Adapter<SetViewHolder> {

    private Context context;
    ArrayList<Item> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

    public AccountDetailsAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.acc_details_card, parent, false);

        SetViewHolder holder = new SetViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(SetViewHolder holder, int position) {
        holder.trans_time.setText(items.get(position).getTransDate());
        holder.trans_desc.setText(items.get(position).getDesc());
        holder.type.setText(items.get(position).getStatus());
       /* if (holder.type.toString().equals("Credit")){
            holder.trans_type.setText("(¢"+Double.parseDouble(items.get(position).getTrans_type())+")");
            holder.type.setTextColor(Color.parseColor("#FF2ECA71"));
        } else {
            holder.trans_type.setText("(¢"+Double.parseDouble(items.get(position).getTrans_type())+")");
            holder.type.setTextColor(Color.parseColor("#FFE54B3D"));
        }
*/
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.rec_color1));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.rec_color2));
        }

        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_bottom);
        holder.itemView.startAnimation(fromRight);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
