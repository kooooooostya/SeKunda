package com.example.sekunda;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekunda.Data.Business;
import com.example.sekunda.Data.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SecFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mActionButtonNew;
    private FloatingActionButton mActionButtonStop;
    private TextView mTextViewSec;
    private TextView mTextViewName;

    private RecyclerAdapter mRecyclerAdapter;

    private Context mContext;

    private boolean isTimerGoing = false;
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

        //TODO при закрытии приложения таймер идет дальше(если это надо)
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
        });

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        mCurrentBusiness = mRecyclerAdapter.getBusinessArrayList().get(viewHolder.getAdapterPosition());
                        mRecyclerAdapter.deleteBusiness(mCurrentBusiness);
                        mTextViewName.setText(mCurrentBusiness.getName());
                        mActionButtonNew.setEnabled(false);
                        mActionButtonStop.setEnabled(true);
                        isTimerGoing = true;

                        break;
                    case ItemTouchHelper.RIGHT:
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
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

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

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        return root;
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
