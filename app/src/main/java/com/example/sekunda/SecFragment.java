package com.example.sekunda;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekunda.Data.Business;
import com.example.sekunda.Data.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SecFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mActionButtonNew;
    private FloatingActionButton mActionButtonStop;
    private TextView mTextViewSec;
    private TextView mTextViewName;

    //TODO sql !!!!!!!!!!!!!!!!!!!!!!

    private RecyclerAdapter mRecyclerAdapter;

    private Context mContext;

    private boolean isTimerGoing = false;
    private int mSeconds;
    private Business mCurrentBusiness;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_sec, container, false);
        mContext = root.getContext();

        mActionButtonNew = root.findViewById(R.id.sec_fab_new);
        mActionButtonStop = root.findViewById(R.id.sec_fab_stop);
        mActionButtonStop.setEnabled(false);
        mRecyclerView = root.findViewById(R.id.sec_recycler_view);
        mTextViewSec = root.findViewById(R.id.sec_text_view_sec);
        mTextViewName = root.findViewById(R.id.sec_text_view_name);

        mRecyclerAdapter = new RecyclerAdapter(mContext);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // Because if don't do this cause null pointer exception
        mCurrentBusiness = new Business("", 0);

        runTimer();
        mActionButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionButtonNew.setEnabled(false);
                mActionButtonStop.setEnabled(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                View inputDialog = LayoutInflater.from(mContext).inflate(R.layout.input_dialog, null);

                builder.setTitle("Write name of task");
                builder.setView(inputDialog);
                final EditText editTextName = inputDialog.findViewById(R.id.input_dialog_editText);

                builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentBusiness = new Business(editTextName.getText().toString(), 0);
                        mTextViewName.setText(editTextName.getText().toString());
                        isTimerGoing = true;
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        mActionButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTimerGoing = false;
                mActionButtonStop.setEnabled(false);
                mActionButtonNew.setEnabled(true);
                mRecyclerAdapter.insertBusiness(mCurrentBusiness);
                mRecyclerAdapter.notifyItemInserted(0);
                mTextViewSec.setText(R.string.time_to_do);
                mTextViewName.setText("");

            }

                // TODO add in sql
        });


        return root;
    }


    private void runTimer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //TODO start timer

                if(isTimerGoing){
                    mTextViewSec.setText(mCurrentBusiness.getTime());
                    mCurrentBusiness.addOneSecond();
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
