package com.jimahtech.banking.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.adapters.MyAdapter;
import com.jimahtech.banking.adapters.MyListAdapter;
import com.jimahtech.banking.manager.PrefManager;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Transfer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {
    Calendar calendar;
    RequestQueue requestQueue;
    EditText startDate, endDate;
    ProgressDialog progressDialog;
    private PrefManager prf;
    Button load;
    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<Item> arrayList = new ArrayList<Item>();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("VIEW TRANSACTIONS");

        calendar = Calendar.getInstance();
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        progressDialog = new ProgressDialog(this);
        load = findViewById(R.id.load);
        recyclerView = findViewById(R.id.recycler_view);
        prf = new PrefManager(this);
        requestQueue = Volley.newRequestQueue(this);

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

        final DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updatedateEndDate();
            }

        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(TransactionActivity.this, datee, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(TransactionActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                LoadView(prf.getString("ID"),startDate.getText().toString(),endDate.getText().toString());
            }
        });
    }
    private void updatedate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(calendar.getTime()));
    }

    private void updatedateEndDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setText(sdf.format(calendar.getTime()));
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

    public void LoadView(String id, String from, String to) {
        progressDialog.setMessage("Logging In,  Please Wait!!!");
        progressDialog.show();
        String url = Config.API_URL + "transaction.php?id=" + id + "&startDate=" + from + "&endDate=" + to;
        if(Config.isDebug)System.out.println("URL: " + url);
        JsonArrayRequest loadRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        arrayList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            final Item item = new Item();
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
                            arrayList.add(item);
                        }
                        linearLayoutManager = new LinearLayoutManager(TransactionActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new MyAdapter(TransactionActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);

                        Animation fromRight = AnimationUtils.loadAnimation(TransactionActivity.this, R.anim.item_animation_from_bottom);
                        MainActivity.amt.startAnimation(fromRight);
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Sorry, you have performed no transactions yet!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    if(Config.isDebug)System.out.println(e.toString());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                if(Config.isDebug)System.out.println(error.toString());
            }
        });
        requestQueue.add(loadRequest);
    }
}

