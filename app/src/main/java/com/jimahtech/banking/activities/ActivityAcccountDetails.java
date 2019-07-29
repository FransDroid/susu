package com.jimahtech.banking.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.adapters.AccountDetailsAdapter;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.manager.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAcccountDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView amt;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    private PrefManager prf;
    private AccountDetailsAdapter adapter;
    private ArrayList<Item> arrayList = new ArrayList<Item>();
    SwipeRefreshLayout swipeLayout;
    private String title = null;
    private String date = null;
    private String code = null;
    private String type = null;
    private double credit = 0;
    private double debit = 0;
    private String desc = null;
    private String status = null;
    TextView codes,datee,typee,amount,desce,statuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acccount_details);

        Bundle extra = getIntent().getExtras();
        try {
            code = extra.getString("Code");
            title = extra.getString("title");
            date = extra.getString("Date");
            type = extra.getString("Type");
            credit = extra.getDouble("Credit");
            debit= extra.getDouble("Debit");
            desc = extra.getString("Desc");
            status = extra.getString("Status");
        } catch (Exception e) {
            if(Config.isDebug)System.out.println(e.toString());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (!(title == null)) {
            setActionBarTitle(title);
        }

        codes = findViewById(R.id.code);
        datee= findViewById(R.id.date);
        typee= findViewById(R.id.type);
        amount= findViewById(R.id.amount);
        desce= findViewById(R.id.desc);
        statuse= findViewById(R.id.status);
        amt = findViewById(R.id.acc_dtl_amount);

        codes.setText(String.format("TRANSACTION CODE: %s", code.toUpperCase()));
        datee.setText(date);
        typee.setText(type);
        if(credit == 0){
           amount.setText(String.format("GHS %s", debit));
            amt.setText(String.format("GHS %s", debit));
        }else if(debit == 0){
           amount.setText(String.format("GHS %s", credit));
            amt.setText(String.format("GHS %s", credit));
        }
        desce.setText(desc);
        statuse.setText(status);
        switch (status) {
            case "Processing":
                statuse.setTextColor(Color.BLUE);
                break;
            case "Completed":
                statuse.setTextColor(Color.GREEN);
                break;
            case "On Hold":
                statuse.setTextColor(Color.RED);
                break;
            case "Declined":
                statuse.setTextColor(Color.RED);
                break;
        }
        swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(ActivityAcccountDetails.this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //recyclerView = findViewById(R.id.recycler_view);
        //requestQueue = Volley.newRequestQueue(this);
        //prf = new PrefManager(getApplicationContext());
        //LoadView(prf.getString("ID"));
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void LoadView(String id) {
        final Item item = new Item();
        String url = Config.API_URL + "transaction.php?CustID=" + id;
        if(Config.isDebug)System.out.println("URL: " + url);
        JsonArrayRequest loadRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        arrayList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);

                            item.setTransDate(jsonObject.getString("TransactionDate"));
                            item.setDesc(jsonObject.getString("Desc"));
                            item.setTransCode(jsonObject.getString("Type"));
                            item.setCredit(jsonObject.getDouble("Amount"));
                            arrayList.add(i, item);
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityAcccountDetails.this);
                        adapter = new AccountDetailsAdapter(ActivityAcccountDetails.this, arrayList);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);

                        amt.setText(String.format("GHS %s", item.getDebit()));
                        Animation fromRight = AnimationUtils.loadAnimation(ActivityAcccountDetails.this, R.anim.item_animation_from_bottom);
                        amt.startAnimation(fromRight);
                    }
                } catch (Exception e) {
                    if(Config.isDebug)System.out.println(e.toString());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                if(Config.isDebug)System.out.println(error.toString());
            }
        });

        requestQueue.add(loadRequest);

    }

    @Override
    public void onRefresh() {
        LoadView(prf.getString("ID"));
        swipeLayout.setRefreshing(false);
    }
}
