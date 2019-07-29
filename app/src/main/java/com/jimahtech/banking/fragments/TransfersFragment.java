package com.jimahtech.banking.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jimahtech.banking.R;
import com.jimahtech.banking.activities.customer.DepositActivity;
import com.jimahtech.banking.activities.SendMoneyActivity;
import com.jimahtech.banking.activities.TransactionActivity;
import com.jimahtech.banking.activities.ViewTransferActivity;
import com.jimahtech.banking.activities.customer.WithdrawActivity;

public class TransfersFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    CardView cardSend, cardWithdraw, cardDeposit, cardTransfer, cardTransaction;

    public TransfersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfers, container, false);
        cardSend = view.findViewById(R.id.cardSendMoney);
        cardWithdraw = view.findViewById(R.id.cardRedraw);
        cardDeposit = view.findViewById(R.id.cardDepositMoney);
        cardTransfer = view.findViewById(R.id.cardViewTransfer);
        cardTransaction = view.findViewById(R.id.cardViewTransactions);

        cardSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
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

        cardDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepositActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        cardTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewTransferActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });


        cardTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
        return view;
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

}
