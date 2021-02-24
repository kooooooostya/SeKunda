package com.example.sekunda.fragments;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekunda.Data.Business;
import com.example.sekunda.Data.BusinessRecyclerAdapter;
import com.example.sekunda.MainActivity;
import com.example.sekunda.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;


import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SecFragment extends Fragment {

    private static final int CHANNEL_ID = 152;

    private FloatingActionButton mActionButtonNew;
    private TextView mTextViewSec;
    private TextView mTextViewName;

    private BusinessRecyclerAdapter mRecyclerAdapter;

    private boolean isTimerGoing = false;
    private Business mCurrentBusiness;
    private int mIndexCurrentBusiness;
    private ViewGroup mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_sec, container, false);

        mContainer = container;
        mActionButtonNew = root.findViewById(R.id.sec_fab_new);

        RecyclerView recyclerView = root.findViewById(R.id.sec_recycler_view);
        mTextViewSec = root.findViewById(R.id.sec_text_view_sec);
        mTextViewName = root.findViewById(R.id.sec_text_view_name);

        mRecyclerAdapter = new BusinessRecyclerAdapter(requireContext());

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
            mActionButtonNew.setImageResource(R.drawable.ic_stop_black_24dp);
        }
        runTimer();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mRecyclerAdapter);

        mActionButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isTimerGoing){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                    View inputDialog = LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog, container, false);

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
                                mActionButtonNew.setImageResource(R.drawable.ic_stop_black_24dp);
                                createNotification();
                            }
                        }
                    });

                    builder.create().show();

                }else{
                    isTimerGoing = false;
                    mCurrentBusiness.setComplete(true);
                    mRecyclerAdapter.changeBusiness(mCurrentBusiness, mIndexCurrentBusiness);
                    mRecyclerAdapter.notifyItemChanged(mIndexCurrentBusiness);
                    mTextViewSec.setText(R.string.time_to_do);
                    mTextViewName.setText("");
                    mActionButtonNew.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    cancelNotification();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
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
                        mActionButtonNew.setImageResource(R.drawable.ic_stop_black_24dp);
                        createNotification();
                    }else {
                        Toast.makeText(requireContext(), "Finish the previous task first", Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }

                    break;
                case ItemTouchHelper.RIGHT:
                    if(!isTimerGoing){
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        final int position = viewHolder.getAdapterPosition();

                        View inputDialog = LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog,  mContainer, false);
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
                        builder.create().show();

                    }else {
                        Toast.makeText(requireContext(), getString(R.string.toast_finish_first), Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight))
                    .addSwipeLeftActionIcon(R.drawable.ic_play_arrow_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                    .addSwipeRightActionIcon(R.drawable.ic_mode_edit_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    });



    private void printInfo(Business business){
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



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(String.valueOf(CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = requireActivity().
                    getSystemService(NotificationManager.class);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
    // Creates notification
    private void createNotification(){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), String.valueOf(CHANNEL_ID));
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(),
                0, MainActivity.newIntent(requireContext()), 0);

        //TODO change icon
        builder.setContentText(getString(R.string.channel_description))
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
        .setContentIntent(pendingIntent)
        .setContentTitle(getString(R.string.app_name))
        .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager)
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(CHANNEL_ID, builder.build());
    }

    //Cancel Notification
    private void cancelNotification(){
        NotificationManager notificationManager = (NotificationManager)
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(CHANNEL_ID);

    }
}
