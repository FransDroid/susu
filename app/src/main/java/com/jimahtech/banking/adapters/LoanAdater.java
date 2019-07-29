package com.jimahtech.banking.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.ActivityAcccountDetails;
import com.jimahtech.banking.activities.ActivitySignIn;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.activities.bank.GrantLoadActivity;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Loans;

import org.json.JSONObject;

import java.util.ArrayList;

public class LoanAdater extends RecyclerView.Adapter<LoanAdater.ViewHolder>{
    private Context context;
    private ArrayList<Loans> items;
    private LayoutInflater inflater;
    private int previousPosition = 0;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView transCode;
        TextView transdate;
        TextView name;
        TextView amount;
        TextView customerId;
        Context context;

        ViewHolder(View itemView, int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            context = c;
            id = (TextView) itemView.findViewById(R.id.id);
            transCode = (TextView) itemView.findViewById(R.id.type);
            name = (TextView) itemView.findViewById(R.id.name);
            transdate = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
            customerId = (TextView) itemView.findViewById(R.id.customerId);
        }
    }

    public LoanAdater(Context context, ArrayList<Loans> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_card, parent, false);
        return new ViewHolder(v, viewType, context); // Returning the created object
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.transCode.setText(items.get(position).getTransCode());
        holder.id.setText(items.get(position).getTransID());
        holder.customerId.setText(items.get(position).getCustomerId());
        holder.name.setText(items.get(position).getName());
        holder.transdate.setText(items.get(position).getTransDate());
        holder.amount.setText(String.format("GHS %s", items.get(position).getCredit()));

        Animation fromRight = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_right);
        holder.itemView.startAnimation(fromRight);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Grant Loan");
                builder.setIcon(R.drawable.grantloan);
                builder.setMessage("Do you want grant loan for "+ items.get(position).getName() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                updateLoan(items.get(position).getTransID(), String.valueOf(items.get(position).getCredit()),items.get(position).getCustomerId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    private void updateLoan(String id ,String amount,String cid) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        final String url = Config.API_URL + "updateLoan.php?id=" + id+"&amt=" + amount + "&cid=" + cid;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        JsonObjectRequest signinRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    boolean success = obj.getBoolean("Success");
                    if (success) {
                        Toast.makeText(context, "Loan Granted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, GrantLoadActivity.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Error Processing Transaction", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(signinRequest);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
