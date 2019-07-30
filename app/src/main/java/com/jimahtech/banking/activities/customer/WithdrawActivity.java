package com.jimahtech.banking.activities.customer;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.manager.PrefManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity {
    Button send;
    RequestQueue requestQueue;
    EditText acNumber, amount, desc,phone;
    ProgressDialog progressDialog;
    private PrefManager prf;
    View focusView = null;
    boolean cancel = false;
    String network;

    String[] data = {"MTN Mobile Money", "Tigo Cash", "Vodafone Cash","Airtel Money"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("WITHDRAW MONEY");

        prf = new PrefManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        send = findViewById(R.id.deposit);
        acNumber = findViewById(R.id.acNumber);
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);
        phone = findViewById(R.id.phone);

        acNumber.setText(prf.getString("AccountNumber"));
        desc.setText("Account Withdrawal");
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_selected, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                network = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        String acno = acNumber.getEditableText().toString().trim();
        String amt = amount.getEditableText().toString().trim();
        String descrp = desc.getEditableText().toString().trim();
        String fone = phone.getEditableText().toString().trim();

        if (TextUtils.isEmpty(amt)) {
            amount.setError("Amount Required!");
            focusView = amount;
            cancel = true;
        } else if (TextUtils.isEmpty(amt)) {
            amount.setError("Amount Required!");
            focusView = amount;
            cancel = true;
        } else if (amt.length() > 4){
            amount.setError("Amount too high");
            focusView = amount;
            cancel = true;
        } else if(TextUtils.isEmpty(descrp)){
            desc.setError("Description cannot be empty!");
            focusView = desc;
            cancel = true;
        }else if(TextUtils.isEmpty(fone)){
            phone.setError("Phone cannot be empty!");
            focusView = phone;
            cancel = true;
        }else if(fone.length() > 12){
            desc.setError("Phone number too long!");
            focusView = desc;
            cancel = true;
        }
        else {
            withdrawMoney(prf.getString("ID"),amt,descrp,network,fone);
        }

    }

    public void ClearEntry() {
        amount.setText("");
        desc.setText("");
        phone.setText("");
    }

    private void withdrawMoney(String id, String amt, String dec,String network,String fone) {
        final String url = Config.API_URL + "Apis.php?func=customerWithdraw";
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
                            System.out.println("URL:" + response);
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
                params.put("id", id);
                params.put("amt", amt);
                params.put("desc", dec);
                params.put("phone", fone);
                params.put("type", network);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }
   /* private void withdrawMoney(String id, String amt, String dec,String network,String fone) {
        final String url = Config.API_URL + "deposit.php?id="+id+"&amt="+amt+"&desc="+dec+"&phone="+fone+"&type="+network;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        JsonObjectRequest sendRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    boolean success = obj.getBoolean("Success");
                    String msg = obj.getString("msg");
                    if(success) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        ClearEntry();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    if(Config.isDebug)System.out.println(e.toString());
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(sendRequest);
    }*/
}

