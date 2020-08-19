package com.example.sekunda;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekunda.Data.Business;
import com.example.sekunda.Data.BusinessRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SecFragment extends Fragment {

    private FloatingActionButton mActionButtonNew;
    private TextView mTextViewSec;
    private TextView mTextViewName;

    private BusinessRecyclerAdapter mRecyclerAdapter;

    private Context mContext;

    private boolean isTimerGoing = false;
    private Business mCurrentBusiness;
    private int mIndexCurrentBusiness;


    private Drawable mDrawableAdd;
    private Drawable mDrawableStop;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_sec, container, false);
        mContext = root.getContext();


        mActionButtonNew = root.findViewById(R.id.sec_fab_new);
        mDrawableAdd = mActionButtonNew.getDrawable();
        mDrawableStop = getActivity().getDrawable(R.drawable.ic_stop_black_24dp);


        RecyclerView recyclerView = root.findViewById(R.id.sec_recycler_view);
        mTextViewSec = root.findViewById(R.id.sec_text_view_sec);
        mTextViewName = root.findViewById(R.id.sec_text_view_name);

        mRecyclerAdapter = new BusinessRecyclerAdapter(mContext);

        try {
            mCurrentBusiness = (Business) mRecyclerAdapter.findIncompleteTask().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        mIndexCurrentBusiness = mRecyclerAdapter.findIndex(mCurrentBusiness);

        if(!mCurrentBusiness.isComplete() && !mCurrentBusiness.getName().equals("")){
            long mil = new Date().getTime() - mCurrentBusiness.getTimeStart().getTime().getTime();
            mCurrentBusiness.setSeconds((int)(mil / 1000));
            printInfo(mCurrentBusiness);
            isTimerGoing = true;
            //TODO image should change
            mActionButtonNew.setBackgroundDrawable(mDrawableStop);
        }
        runTimer();

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mRecyclerAdapter);

        mActionButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isTimerGoing){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    View inputDialog = LayoutInflater.from(mContext).inflate(R.layout.input_dialog, null);

                    builder.setTitle("Write name of task");
                    builder.setView(inputDialog);
                    final EditText editTextName = inputDialog.findViewById(R.id.input_dialog_editText);

                    builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(editTextName.getText().length() != 0){

                                Business business = new Business(editTextName.getText().toString(), 0);
                                business.setComplete(false);
                                try {
                                    mCurrentBusiness = (Business)mRecyclerAdapter.insertBusiness(business).clone();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                mIndexCurrentBusiness = mRecyclerAdapter.findIndex(mCurrentBusiness);
                                mTextViewName.setText(mCurrentBusiness.getName());
                                isTimerGoing = true;
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    mActionButtonNew.setBackgroundDrawable(mDrawableStop);
                }else{
                    isTimerGoing = false;
                    mCurrentBusiness.setComplete(true);
                    mRecyclerAdapter.changeBusiness(mCurrentBusiness, mIndexCurrentBusiness);
                    mRecyclerAdapter.notifyItemChanged(mIndexCurrentBusiness);
                    mTextViewSec.setText(R.string.time_to_do);
                    mTextViewName.setText("");
                    mActionButtonNew.setBackgroundDrawable(mDrawableAdd);
                }
            }
        });


        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        if(!isTimerGoing){
                            try {
                                mCurrentBusiness = (Business)mRecyclerAdapter.getBusinessArrayList().get(viewHolder.getAdapterPosition()).clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            mCurrentBusiness.setComplete(false);
                            mIndexCurrentBusiness = mRecyclerAdapter.findIndex(mCurrentBusiness);

                            mTextViewName.setText(mCurrentBusiness.getName());
                            isTimerGoing = true;

                        }else {
                            Toast.makeText(mContext, "Finish the previous task first", Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }

                        break;
                    case ItemTouchHelper.RIGHT:
                        if(!isTimerGoing){
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            final int position = viewHolder.getAdapterPosition();

                            View inputDialog = LayoutInflater.from(mContext).inflate(R.layout.input_dialog, null);
                            final EditText editTextName = inputDialog.findViewById(R.id.input_dialog_editText);

                            builder.setTitle("Rename task");
                            builder.setView(inputDialog);
                            editTextName.setText(mCurrentBusiness.getName());

                            try {
                                mCurrentBusiness = (Business)mRecyclerAdapter.getBusinessArrayList().get(position).clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mCurrentBusiness.setName(editTextName.getText().toString());
                                    mRecyclerAdapter.changeBusiness(mCurrentBusiness, position);
                                    mRecyclerAdapter.notifyItemChanged(position);

                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }else {
                            Toast.makeText(mContext, "Finish the previous task first", Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_play_arrow_black_24dp)
                        .addSwipeLeftBackgroundColor(R.color.colorPrimary)
                        .addSwipeRightActionIcon(R.drawable.ic_mode_edit_black_24dp)
                        .addSwipeRightBackgroundColor(R.color.colorAccent)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        return root;
    }

    void printInfo(Business business){
        mTextViewName.setText(business.getName());
        mTextViewSec.setText(business.getTime());
    }

    private void runTimer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isTimerGoing){
                    mTextViewSec.setText(mCurrentBusiness.getTime());
                    mCurrentBusiness.addOneSecond();
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
