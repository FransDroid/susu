package com.jimahtech.banking.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jimahtech.banking.Config.Config;
import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.customer.BalanceActivity;
import com.jimahtech.banking.activities.customer.BonusActivity;
import com.jimahtech.banking.activities.customer.DepositActivity;
import com.jimahtech.banking.activities.MainActivity;
import com.jimahtech.banking.activities.customer.LoanActivity;
import com.jimahtech.banking.activities.customer.StatementActivity;
import com.jimahtech.banking.activities.customer.WithdrawActivity;
import com.jimahtech.banking.adapters.AccountAdapter;
import com.jimahtech.banking.model.Item;
import com.jimahtech.banking.manager.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentAccount extends Fragment {
    private OnFragmentInteractionListener mListener;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    AccountAdapter adapter;
    ArrayList<Item> arrayList;
    CircleImageView photo;
    LinearLayoutManager linearLayoutManager;
    TextView name,account,account2,dob,gender,email,address,phone;
    CardView cardDeposit,cardWithdraw,cardBalance,cardLoan,cardStatemet,cardBonus;

    public FragmentAccount() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        recyclerView = view.findViewById(R.id.recycler_view);
        name = view.findViewById(R.id.acName);
        account = view.findViewById(R.id.acNumber);
        account2 = view.findViewById(R.id.acNumber2);
        dob = view.findViewById(R.id.dob);
        gender = view.findViewById(R.id.gender);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        photo = view.findViewById(R.id.photo);
        PrefManager prf = new PrefManager(getContext());

        cardDeposit = view.findViewById(R.id.cardDepositMoney);
        cardWithdraw= view.findViewById(R.id.cardWithdraw);
        cardBalance= view.findViewById(R.id.cardBalance);
        cardLoan= view.findViewById(R.id.cardLoan);
        cardStatemet= view.findViewById(R.id.cardStatement);
        cardBonus= view.findViewById(R.id.cardBonus);

        cardDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepositActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
        cardWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WithdrawActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        cardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BalanceActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        cardLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoanActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        cardStatemet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatementActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        cardBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BonusActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        readLocal(prf);

        return view;
    }

    public void readLocal(PrefManager prf){
        name.setText(String.format("%s %s", prf.getString("FirstName"), prf.getString("LastName")));
        account.setText(prf.getString("AccountNumber"));
        account2.setText(prf.getString("AccountNumber"));
        dob.setText(prf.getString("DOB"));
        gender.setText(prf.getString("Gender"));
        email.setText(prf.getString("Email"));
        address.setText(prf.getString("Address"));
        phone.setText(prf.getString("Phone"));
        Picasso.with(getContext()).load(Config.IMAGES_URL + prf.getString("Photo")).placeholder(R.drawable.header).into(photo);
        if(Config.isDebug)System.out.println("Pic:" +Config.IMAGES_URL + prf.getString("Photo") );

        //LoadView(prf.getString("ID"));
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                        linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new AccountAdapter(getContext(), arrayList);
                        recyclerView.setAdapter(adapter);


                        //MainActivity.amt.setText(String.format("GHS %s", item.getTotal_amount()));
                        Animation fromRight = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation_from_bottom);
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
