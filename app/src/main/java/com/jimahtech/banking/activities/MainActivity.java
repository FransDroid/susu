package com.jimahtech.banking.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.jimahtech.banking.activities.agents.DailyReceiptActivity;
import com.jimahtech.banking.activities.agents.DialyPaymentActivty;
import com.jimahtech.banking.activities.agents.NewCustomerActivity;
import com.jimahtech.banking.activities.bank.BankRegisterAgent;
import com.jimahtech.banking.activities.bank.GrantLoadActivity;
import com.jimahtech.banking.fragments.FragmentAccount;
import com.jimahtech.banking.manager.PrefManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentAccount.OnFragmentInteractionListener {

    NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    SwipeRefreshLayout swipeLayout;
    public static TextView amt = null;
    private PrefManager prf;
    CardView cardNew, cardPayment, cardBalance, cardWithdraw, cardReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prf = new PrefManager(this);

        if (prf.getString("Status").equals("2")) {
            //Status 2 Agent And 3 is Bank
            setContentView(R.layout.activity_main_agent);

            /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);*/
            setActionBarTitle("SUSU MOBILE(AGENT)");

            cardNew = findViewById(R.id.cardNew);
            cardPayment = findViewById(R.id.cardPayment);
            cardBalance = findViewById(R.id.cardBalance);
            cardReceipt = findViewById(R.id.cardReceipt);

            cardReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DailyReceiptActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
            });

            cardBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.custom_dialog3);
                    dialog.setCancelable(false);
                    Button cancel_action = dialog.findViewById(R.id.cancel_action);
                    Button load = dialog.findViewById(R.id.load);
                    final EditText dialogId = dialog.findViewById(R.id.dialogId);
                    cancel_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogId.getText().toString().isEmpty()) {
                                dialogId.setError("Account Number Required");
                            } else {
                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                final String url = Config.API_URL + "Apis.php?func=getCustomerBalance&id=" + dialogId.getText().toString();
                                // Showing progress dialog at user registration time.
                                progressDialog.setMessage("Processing Transaction,  Please Wait!!!");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                if(Config.isDebug)System.out.println("URL: " + url);
                                JsonObjectRequest signinRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progressDialog.dismiss();
                                        try {
                                            boolean Success = response.getBoolean("Success");
                                            if (Success && response.getString("AccountNumber").isEmpty()) {
                                                dialog.dismiss();
                                                progressDialog.dismiss();
                                                    final Dialog dialog1 = new Dialog(MainActivity.this);
                                                    dialog1.setContentView(R.layout.custom_dialog4);
                                                    dialog1.setCancelable(false);
                                                    Button cancel_action = dialog1.findViewById(R.id.cancel_action);
                                                    TextView name = dialog1.findViewById(R.id.name);
                                                    TextView acno = dialog1.findViewById(R.id.accounts);
                                                    TextView debit = dialog1.findViewById(R.id.debit);
                                                    TextView credit = dialog1.findViewById(R.id.credit);
                                                    TextView total = dialog1.findViewById(R.id.total);

                                                    acno.setText(response.getString("AccountNumber"));
                                                    name.setText(String.format("%s %s", response.getString("FirstName"), response.getString("LastName")));
                                                    debit.setText(String.format("GHS %s", response.getString("Debit")));
                                                    credit.setText(String.format("GHS %s", response.getString("Credit")));
                                                    total.setText(String.format("GHS %s", response.getString("total")));
                                                    cancel_action.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog1.dismiss();
                                                        }
                                                    });
                                                    dialog1.show();
                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Sorry, Accounts Number Not Found!!", Toast.LENGTH_LONG).show();
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

                                requestQueue.add(signinRequest);

                            }
                        }
                    });
                    dialog.show();
                }
            });
            cardPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.custom_dialog2);
                    dialog.setCancelable(false);
                    Button cancel_action = dialog.findViewById(R.id.cancel_action);
                    Button load = dialog.findViewById(R.id.load);
                    final EditText dialogId = dialog.findViewById(R.id.dialogId);
                    cancel_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogId.getText().toString().isEmpty()) {
                                dialogId.setError("ID or Email Required");
                            } else {
                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                final String url = Config.API_URL + "Apis.php?func=getCustomer";
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
                                                    if (response.equals("[]")) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Sorry, No Customer Found!!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(MainActivity.this, DialyPaymentActivty.class);
                                                        intent.putExtra("data", response);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.open_next, R.anim.close_next);
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
                                        params.put("id", dialogId.getText().toString().trim());
                                        return params;
                                    }
                                };
                                requestQueue.add(postRequest);
                            }
                        }
                    });
                    dialog.show();
                }
            });

            cardNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setCancelable(false);
                    LinearLayout addNew = dialog.findViewById(R.id.addCustomer);
                    LinearLayout updateNew = dialog.findViewById(R.id.updateNew);
                    final LinearLayout layoutMain = dialog.findViewById(R.id.layoutMain);
                    final LinearLayout layoutId = dialog.findViewById(R.id.layoutID);
                    Button cancel = dialog.findViewById(R.id.cancel);
                    Button cancel_action = dialog.findViewById(R.id.cancel_action);
                    Button load = dialog.findViewById(R.id.load);
                    ImageButton close = dialog.findViewById(R.id.close);
                    final EditText dialogId = dialog.findViewById(R.id.dialogId);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    cancel_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutMain.setVisibility(View.VISIBLE);
                            layoutId.setVisibility(View.GONE);
                        }
                    });
                    addNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, NewCustomerActivity.class);
                            intent.putExtra("title", "REGISTER NEW CUSTOMER");
                            intent.putExtra("type", false);
                            startActivity(intent);
                            overridePendingTransition(R.anim.open_next, R.anim.close_next);
                        }
                    });
                    updateNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutMain.setVisibility(View.GONE);
                            layoutId.setVisibility(View.VISIBLE);
                        }
                    });
                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogId.getText().toString().isEmpty()) {
                                dialogId.setError("ID or Email Required");
                            } else {
                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                final String url = Config.API_URL + "Apis.php?func=getCustomer";
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
                                                    if (response.equals("[]")) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Sorry, No Customer Found!!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(MainActivity.this, NewCustomerActivity.class);
                                                        intent.putExtra("title", "UPDATE NEW CUSTOMER");
                                                        intent.putExtra("type", true);
                                                        intent.putExtra("data", response);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.open_next, R.anim.close_next);
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
                                        params.put("id", dialogId.getText().toString().trim());
                                        return params;
                                    }
                                };
                                requestQueue.add(postRequest);
                            }
                        }
                    });
                    dialog.show();
                }
            });


        } else if (prf.getString("Status").equals("3")) {
            setContentView(R.layout.actvity_main_bank);

            setActionBarTitle("SUSU MOBILE(BANK)");

            cardNew = findViewById(R.id.cardNew);
            cardPayment = findViewById(R.id.cardLoan);
            cardBalance = findViewById(R.id.cardPayment);

            cardNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BankRegisterAgent.class);
                    intent.putExtra("title", "REGISTER NEW AGENT");
                    intent.putExtra("type", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
            });

            cardPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GrantLoadActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
            });
            cardBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BankRegisterAgent.class);
                    intent.putExtra("title", "CHECK DAILY PAYMENT");
                    intent.putExtra("type", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
            });
        } else {
            //CUSTOMER VIEW
            setContentView(R.layout.activity_main);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setActionBarTitle("SUSU MOBILE(CUSTOMER)");

            amt = findViewById(R.id.toolbar_amount);
       /* swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(MainActivity.this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/

            //load the home fragment here - welcome page
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = FragmentAccount.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }


            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            /*mDrawerLayout = findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();

            setupDrawer();
            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);*/

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setActionBarTitle("ACCOUNTS");
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void clearPrf() {
        prf.remove("ID");
        prf.remove("AccountNumber");
        prf.remove("Photo");
        prf.remove("FirstName");
        prf.remove("LastName");
        prf.remove("DOB");
        prf.remove("FullName");
        prf.remove("Gender");
        prf.remove("Phone");
        prf.remove("Email");
        prf.remove("Address");
        prf.remove("Status");
        prf.remove("IS_LOG_IN");
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /*  @Override
      protected void onPostCreate(Bundle savedInstanceState) {
          super.onPostCreate(savedInstanceState);
          // Sync the toggle state after onRestoreInstanceState has occurred.
          mDrawerToggle.syncState();
      }

      @Override
      public void onConfigurationChanged(Configuration newConfig) {
          super.onConfigurationChanged(newConfig);
          mDrawerToggle.onConfigurationChanged(newConfig);
      }
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_reload) {
           /* FragmentAccount adds = new FragmentAccount();
            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentContainer, adds)
                    .addToBackStack(null)
                    .commit();*/

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Do you want to sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            clearPrf();
                            finish();
                            Intent intent = new Intent(MainActivity.this, ActivitySignIn.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.open_next, R.anim.close_next);
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
        // Activate the navigation drawer toggle
    /*    if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;


        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_page:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.action_transfer:
                intent = new Intent(this, ActivityTransfer.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.action_setting:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /*@Override
    public void onRefresh() {
        FragmentAccount adds = new FragmentAccount();
        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, adds)
                .addToBackStack(null)
                .commit();
        swipeLayout.setRefreshing(false);
    }*/
}
