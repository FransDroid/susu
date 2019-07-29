package com.jimahtech.banking.activities.agents;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.adapters.ReceiptAdapter;
import com.jimahtech.banking.manager.PrefManager;
import com.jimahtech.banking.model.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyReceiptActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    ReceiptAdapter adapter;
    ArrayList<Statement> arrayList;
    TextView total,txtcredit,txtdebit;
    LinearLayoutManager linearLayoutManager;
    Calendar calendar;
    EditText startDate;
    PrefManager prf;
    LinearLayout noLayout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_receipt);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("DAILY RECEIPTS");

        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recycler_view);
        calendar = Calendar.getInstance();
        startDate = findViewById(R.id.startDate);
        total = findViewById(R.id.total_amount);
        txtcredit = findViewById(R.id.credit);
        txtdebit = findViewById(R.id.debit);
        noLayout = findViewById(R.id.noLayout);
        prf = new PrefManager(DailyReceiptActivity.this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updatedate();
            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DailyReceiptActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String date_n = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        startDate.setText(date_n);

        LoadView(prf.getString("ID"),date_n);
    }

    private void updatedate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(calendar.getTime()));
        LoadView(prf.getString("ID"),startDate.getText().toString());
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
    private void LoadView(String id, String date) {
        arrayList = new ArrayList<Statement>();
        String url = Config.API_URL + "Apis.php?func=getDailyReceipt&id=" + id+"&date="+date;
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
        progressDialog.show();

        if(Config.isDebug)System.out.println("URL: " + url);
        JsonObjectRequest sendRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.length() > 0) {
                        arrayList.clear();
                        double debit = 0;
                        double credit = 0;
                        JSONObject obj = new JSONObject(String.valueOf(response));
                        boolean success = obj.getBoolean("Success");
                        if (success) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noLayout.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            JSONArray data = obj.getJSONArray("Data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject = data.getJSONObject(i);
                                final Statement item = new Statement();
                                // if(jsonObject.getString("Status").equals("Completed")){
                                item.setTransId(jsonObject.getInt("TransactionID"));
                                item.setTransDate(jsonObject.getString("TransactionDate"));
                                item.setTransCode(jsonObject.getString("TransactionCode"));
                                item.setCredit(jsonObject.getDouble("Credit"));
                                item.setDebit(jsonObject.getDouble("Debit"));
                                item.setDesc(jsonObject.getString("Desc"));
                                item.setStatus(jsonObject.getString("Status"));
                                if(jsonObject.getDouble("Debit") == 0){
                                    item.setType("Deposit");
                                }else if(jsonObject.getDouble("Credit") == 0){
                                    item.setType("Withdrawal");
                                }
                                debit = debit + jsonObject.getDouble("Debit");
                                credit = credit+ jsonObject.getDouble("Credit");
                                item.setName(jsonObject.getString("FirstName")+ " "+ jsonObject.getString("LastName"));
                                arrayList.add(item);
                                //}
                            }
                        }else {
                            progressDialog.dismiss();
                            recyclerView.setVisibility(View.GONE);
                            noLayout.setVisibility(View.VISIBLE);
                        }
                        linearLayoutManager = new LinearLayoutManager(DailyReceiptActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new ReceiptAdapter(DailyReceiptActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);

                        txtcredit.setText(String.format("Credit GHS %s", credit));
                        txtdebit.setText(String.format("Debit GHS %s",debit));
                        total.setText(String.format("GHS %s", credit - debit));

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
                if(Config.isDebug)System.out.println(error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(sendRequest);
    }
}
