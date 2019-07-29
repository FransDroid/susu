package com.jimahtech.banking.activities.bank;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.EditText;
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
import com.jimahtech.banking.activities.customer.StatementActivity;
import com.jimahtech.banking.adapters.BankAdapter;
import com.jimahtech.banking.adapters.MyListAdapter;
import com.jimahtech.banking.adapters.StatementAdapter;
import com.jimahtech.banking.manager.PrefManager;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.model.Names;
import com.jimahtech.banking.model.Transfer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BankRegisterAgent extends AppCompatActivity {
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private PrefManager prf;
    Button register;
    RecyclerView recyclerView;
    BankAdapter adapter;
    ArrayList<Names> arrayList = new ArrayList<Names>();
    LinearLayoutManager linearLayoutManager;
    String title = null;
    TextView subHeader;
    boolean type = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_register_agent2);

        Bundle extra = getIntent().getExtras();
        try {
            title = extra.getString("title");
            type = extra.getBoolean("type");
        } catch (Exception e) {
            if(Config.isDebug)System.out.println(e.toString());
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle(title);

        progressDialog = new ProgressDialog(this);
        register = findViewById(R.id.register);
        subHeader = findViewById(R.id.subHeader);
        LinearLayout layout2 = findViewById(R.id.layout2);
        recyclerView = findViewById(R.id.recycler_view);
        prf = new PrefManager(this);
        requestQueue = Volley.newRequestQueue(this);

        layout2.setVisibility(View.VISIBLE);

        if(type){
            layout2.setVisibility(View.GONE);
            subHeader.setText("Please use the button below to register new agent. Select and an agent from the list to check balance");
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankRegisterAgent.this, NewAgentAgent.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });


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
        arrayList = new ArrayList<Names>();
        String url = Config.API_URL + "getAgents.php";
        if(Config.isDebug)System.out.println("URL: " + url);
        JsonArrayRequest loadRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        arrayList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            final Names item = new Names();
                            item.setId(jsonObject.getString("AdminID"));
                            item.setName(jsonObject.getString("FullName"));
                            item.setEmail(jsonObject.getString("Email"));
                            item.setGender(jsonObject.getString("Gender"));
                            item.setDob(jsonObject.getString("dob"));
                            item.setAddress(jsonObject.getString("address"));
                            item.setPhone(jsonObject.getString("phone"));
                            item.setPhoto(jsonObject.getString("photo"));

                                arrayList.add(item);
                        }
                        linearLayoutManager = new LinearLayoutManager(BankRegisterAgent.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new BankAdapter(BankRegisterAgent.this, arrayList,type);
                        recyclerView.setAdapter(adapter);

                        //MainActivity.amt.setText(String.format("GHS %s", item.getTotal_amount()));
                        Animation fromRight = AnimationUtils.loadAnimation(BankRegisterAgent.this, R.anim.item_animation_from_bottom);
                        MainActivity.amt.startAnimation(fromRight);
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
