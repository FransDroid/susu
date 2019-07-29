package com.jimahtech.banking.activities.bank;

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
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.adapters.BankAdapter;
import com.jimahtech.banking.adapters.LoanAdater;
import com.jimahtech.banking.manager.PrefManager;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Loans;
import com.jimahtech.banking.model.Names;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GrantLoadActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private PrefManager prf;
    Button register;
    RecyclerView recyclerView;
    LoanAdater adapter;
    ArrayList<Loans> arrayList = new ArrayList<Loans>();
    LinearLayoutManager linearLayoutManager;
    LinearLayout noLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_load);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("GRANT LOAN");

        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recycler_view);
        noLayout = findViewById(R.id.noLayout);
        prf = new PrefManager(this);
        requestQueue = Volley.newRequestQueue(this);

        LoadView();
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
    public void LoadView() {
        arrayList = new ArrayList<Loans>();
        String url = Config.API_URL + "getLoans.php";
        if(Config.isDebug)System.out.println("URL: " + url);
        JsonArrayRequest loadRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        arrayList.clear();
                        recyclerView.setVisibility(View.VISIBLE);
                        noLayout.setVisibility(View.GONE);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            final Loans item = new Loans();
                            item.setTransID(jsonObject.getString("TransactionID"));
                            item.setTransDate(jsonObject.getString("TransactionDate"));
                            item.setTransCode(jsonObject.getString("TransactionCode"));
                            item.setCredit(jsonObject.getDouble("Credit"));
                            item.setCustomerId(jsonObject.getString("CustID"));
                            item.setName(jsonObject.getString("FirstName") + " " + jsonObject.getString("LastName"));

                            arrayList.add(item);
                        }
                        linearLayoutManager = new LinearLayoutManager(GrantLoadActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new LoanAdater(GrantLoadActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);

                        //MainActivity.amt.setText(String.format("GHS %s", item.getTotal_amount()));
                        Animation fromRight = AnimationUtils.loadAnimation(GrantLoadActivity.this, R.anim.item_animation_from_bottom);
                        MainActivity.amt.startAnimation(fromRight);
                    }else {
                        recyclerView.setVisibility(View.GONE);
                        noLayout.setVisibility(View.VISIBLE);
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

}
