package com.jimahtech.banking.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.manager.PrefManager;

import org.json.JSONObject;
import org.json.JSONTokener;

public class SendMoneyActivity extends AppCompatActivity {
    Button send;
    RequestQueue requestQueue;
    EditText acNumber, amount, desc;
    ProgressDialog progressDialog;
    private PrefManager prf;
    View focusView = null;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("SEND MONEY");

        prf = new PrefManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        send = findViewById(R.id.send);
        acNumber = findViewById(R.id.acNumber);
        amount = findViewById(R.id.amount);
        desc = findViewById(R.id.desc);


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

        if (TextUtils.isEmpty(acno.trim())) {
            acNumber.setError("Account Number Required!");
            focusView = acNumber;
            cancel = true;
        } else if (acno.equals(prf.getString("AccountNumber"))) {
            acNumber.setError("You cannot send money to yourself");
            focusView = acNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(amt)) {
            amount.setError("Amount Required!");
            focusView = amount;
            cancel = true;
        } else if (amt.length() > 4){
            amount.setError("Amount too high");
            focusView = amount;
            cancel = true;
        }else {
            sendMoney(prf.getString("ID"),acno,amt,descrp);
        }

    }

    public void ClearEntry() {
        acNumber.setText("");
        amount.setText("");
        desc.setText("");
    }

    private void sendMoney(String id, String acn, String amt, String dec) {
        final String url = Config.API_URL + "send.php?id=" + id + "&account=" + acn + "&amount=" + amt + "&desc=" + dec;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.show();
        String msg;

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
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        ClearEntry();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
    }

}
