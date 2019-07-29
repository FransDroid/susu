package com.jimahtech.banking.activities.agents;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.ActivitySignIn;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.manager.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialyPaymentActivty extends AppCompatActivity {
    Button send;
    RequestQueue requestQueue;
    EditText acNumber, amount, desc;
    ProgressDialog progressDialog;
    private PrefManager prf;
    View focusView = null;
    boolean cancel = false;
    String jsonObject = null;
    String cusID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialy_payment_activty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("DAILY PAYMENTS");

        Bundle extra = getIntent().getExtras();
        try {
            jsonObject = extra.getString("data");
        } catch (Exception e) {
            if(Config.isDebug)System.out.println(e.toString());
        }

        prf = new PrefManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        send = findViewById(R.id.deposit);
        acNumber = findViewById(R.id.acNumber);
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);

        desc.setText("Daily Payment");
        try {
            JSONObject json = new JSONObject(jsonObject.replace("[", "").replace("]", ""));
            for (int i = 0; i < json.length(); i++) {
                cusID = json.getString("CustomerID");
                acNumber.setText(json.getString("AccountNumber"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void saveData() {
        final String amt = amount.getEditableText().toString().trim();
        String decc = desc.getEditableText().toString().trim();
        if (TextUtils.isEmpty(amt)) {
            amount.setError("Amount Required!");
            focusView = amount;
            cancel = true;
        } else if (amt.length() > 4) {
            amount.setError("Amount too long!");
            focusView = amount;
            cancel = true;
        } else {


            AlertDialog.Builder builder = new AlertDialog.Builder(DialyPaymentActivty.this);
            builder.setTitle("Confirm Deposit?");
            builder.setIcon(R.drawable.deposit);
            builder.setMessage("Do you want to deposit GHS:" + amt +" to Account Number " + acNumber.getText().toString()+" ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            depositAmount(cusID,amt,desc.getText().toString());
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

    }

    public void ClearEntry() {
        amount.setText("");
    }

    private void depositAmount(final String id, final String amt, final String desc) {
        final String url = Config.API_URL + "Apis.php?func=dailyPayment";
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(String.valueOf(response));
                            boolean success = obj.getBoolean("Success");
                            String msg = obj.getString("msg");
                            if (success) {
                                progressDialog.dismiss();
                                if(Config.isDebug)System.out.println("ERROE" + response);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                ClearEntry();
                                //Config.sendSMS("Dear Customer a payment of GHS "+ amt+" has been successfully deposited into you account","Susu Mobile","00233246275242");
                            } else {
                                progressDialog.dismiss();
                                if(Config.isDebug)System.out.println("ERROE" + response);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            if(Config.isDebug)System.out.println(e.toString());
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("aid", prf.getString("ID"));
                params.put("id", id);
                params.put("amount", amt);
                params.put("desc", desc);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

}
