package com.jimahtech.banking.activities.customer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.adapters.AccountAdapter;
import com.jimahtech.banking.adapters.StatementAdapter;
import com.jimahtech.banking.manager.PrefManager;
import com.jimahtech.banking.model.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatementActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    StatementAdapter adapter;
    ArrayList<Item> arrayList;
    TextView total,txtcredit,txtdebit;
    LinearLayoutManager linearLayoutManager;
    LinearLayout noLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("STATEMENT");

        requestQueue = Volley.newRequestQueue(StatementActivity.this);
        recyclerView = findViewById(R.id.recycler_view);
        total = findViewById(R.id.total_amount);
        noLayout = findViewById(R.id.noLayout);
        txtcredit = findViewById(R.id.credit);
        txtdebit = findViewById(R.id.debit);
        PrefManager prf = new PrefManager(StatementActivity.this);


        LoadView(prf.getString("ID"));

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
    public void LoadView(String id) {
        arrayList = new ArrayList<Item>();
        String url = Config.API_URL + "statement.php?id=" + id;
        if(Config.isDebug)System.out.println("URL: " + url);
        JsonArrayRequest loadRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        noLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        double debit = 0;
                        double credit = 0;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            final Item item = new Item();
                            if(jsonObject.getString("Status").equals("Completed")){
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
                                arrayList.add(item);
                            }
                        }
                        linearLayoutManager = new LinearLayoutManager(StatementActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new StatementAdapter(StatementActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);

                        txtcredit.setText(String.format("Credit GHS %s", credit));
                        txtdebit.setText(String.format("Debit GHS %s",debit));
                        total.setText(String.format("GHS %s", credit - debit));


                        //MainActivity.amt.setText(String.format("GHS %s", item.getTotal_amount()));
                        Animation fromRight = AnimationUtils.loadAnimation(StatementActivity.this, R.anim.item_animation_from_bottom);
                        MainActivity.amt.startAnimation(fromRight);
                    }else{
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
