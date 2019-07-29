package com.jimahtech.banking.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jimahtech.banking.R;

/**
 * Created by Chief on 2017-10-31.
 */

public class SetViewHolder extends RecyclerView.ViewHolder{
    public TextView account_number;
    public TextView account_type;
    public TextView total_amount;

    public TextView trans_time;
    public TextView trans_desc;
    public TextView trans_type;
    public TextView type;


    public SetViewHolder(View itemView) {
        super(itemView);

        account_number = itemView.findViewById(R.id.account_number);
        account_type = itemView.findViewById(R.id.account_type);
        total_amount = itemView.findViewById(R.id.total_amount);

        trans_time = itemView.findViewById(R.id.trans_time);
        trans_desc = itemView.findViewById(R.id.trans_desc);
        trans_type = itemView.findViewById(R.id.trans_type);
        type = itemView.findViewById(R.id.type);

    }
}
