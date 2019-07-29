package com.jimahtech.banking.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.ActivityAcccountDetails;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.activities.agents.NewCustomerActivity;
import com.jimahtech.banking.activities.bank.BankReceiptActivity;
import com.jimahtech.banking.activities.bank.UpdateAgentActivity;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Names;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Names> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;
    boolean type;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView name;
        TextView email;
        TextView gender;
        TextView dob;
        TextView address;
        TextView phone;
        ImageView photo;
        TextView status;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            gender = (TextView) itemView.findViewById(R.id.gender);
            dob = (TextView) itemView.findViewById(R.id.dob);
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            photo = (ImageView) itemView.findViewById(R.id.profilePic);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }

    public BankAdapter(Context context, ArrayList<Names> items,boolean type) {
        this.context = context;
        this.items = items;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_card, parent, false);
        return new ViewHolder(v, viewType, context); // Returning the created object
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.id.setText(items.get(position).getId());
        holder.name.setText(items.get(position).getName());
        holder.email.setText(items.get(position).getEmail());
        holder.gender.setText(items.get(position).getGender());
        holder.dob.setText(items.get(position).getDob());
        holder.address.setText(items.get(position).getAddress());
        holder.phone.setText(items.get(position).getPhone());

        Picasso.with(context).load(Config.IMAGES_URL + items.get(position).getPhoto()).placeholder(R.drawable.header).into(holder.photo);
        if(Config.isDebug)System.out.println("Pic:" +Config.IMAGES_URL + items.get(position).getPhoto() );
        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_right);
        holder.itemView.startAnimation(fromRight);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type){
                    final Dialog dialog = new Dialog(context);
                    Calendar calendar = Calendar.getInstance();
                    dialog.setContentView(R.layout.custom_dialog5);
                    dialog.setCancelable(false);
                    Button cancel_action = dialog.findViewById(R.id.cancel_action);
                    Button load = dialog.findViewById(R.id.load);
                    final EditText dialogId = dialog.findViewById(R.id.dialogId);
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updatedate(calendar, dialogId);
                        }

                    };
                    dialogId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            new DatePickerDialog(context, date, calendar
                                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    cancel_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogId.getText().toString().isEmpty()) {
                                dialogId.setError("Date Number Required");
                            } else {
                                dialog.dismiss();
                                Intent intent = new Intent(context, BankReceiptActivity.class);
                                intent.putExtra("id",items.get(position).getId());
                                intent.putExtra("date", dialogId.getText().toString());
                                context.startActivity(intent);
                            }
                        }
                    });
                    dialog.show();
                }else {
                    Intent intent = new Intent(context, UpdateAgentActivity.class);
                    intent.putExtra("id",items.get(position).getId());
                    intent.putExtra("name", items.get(position).getName());
                    intent.putExtra("email", items.get(position).getEmail());
                    intent.putExtra("gender", items.get(position).getGender());
                    intent.putExtra("dob", items.get(position).getDob());
                    intent.putExtra("address", items.get(position).getAddress());
                    intent.putExtra("phone", items.get(position).getPhone());
                    intent.putExtra("photo", items.get(position).getPhoto());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updatedate(Calendar calendar,EditText dob) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(calendar.getTime()));
    }


}
