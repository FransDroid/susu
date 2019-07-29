package com.jimahtech.banking.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import com.jimahtech.banking.R;
import com.jimahtech.banking.fragments.FragmentAccount;
import com.jimahtech.banking.fragments.SendMoneyFragment;
import com.jimahtech.banking.fragments.TransfersFragment;

public class ActivityTransfer extends AppCompatActivity implements
        TransfersFragment.OnFragmentInteractionListener, SendMoneyFragment.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle("TRANSFER");

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = TransfersFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
