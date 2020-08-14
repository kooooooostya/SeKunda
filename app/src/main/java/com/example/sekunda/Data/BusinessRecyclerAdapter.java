package com.example.sekunda.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekunda.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BusinessRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<Business> mBusinessArrayList;
    private BusinessSQLiteOpenHelper mSQLiteOpenHelper;

    public BusinessRecyclerAdapter(Context context) {
        BusinessSQLiteOpenHelper SQLiteOpenHelper = new BusinessSQLiteOpenHelper(context);
        mSQLiteOpenHelper = new BusinessSQLiteOpenHelper(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mBusinessArrayList = SQLiteOpenHelper.getFulledList(calendar);
    }

    public BusinessRecyclerAdapter(ArrayList<Business> arrayList) {
        mBusinessArrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_item, parent, false);
        return new BusinessViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BusinessViewHolder businessViewHolder = (BusinessViewHolder)holder;
        businessViewHolder.mTextViewTime.setText(mBusinessArrayList.get(position).getTime());
        businessViewHolder.mTextViewName.setText(mBusinessArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mBusinessArrayList.size();
    }

    public ArrayList<Business> getBusinessArrayList(){
        return mBusinessArrayList;
    }



    public void insertBusiness(Business business){
        mBusinessArrayList.add(0, business);
        mSQLiteOpenHelper.insertBusinessAsync(business);
    }
    public void changeBusiness(Business newBusiness, int index){
        mSQLiteOpenHelper.changeAsync(newBusiness, mBusinessArrayList.get(index));
        mBusinessArrayList.set(index, newBusiness);
    }
    public void deleteBusiness(Business business){
        mSQLiteOpenHelper.deleteBusinessAsync(business);
        mBusinessArrayList.remove(business);
    }
}

class BusinessViewHolder extends RecyclerView.ViewHolder{

    TextView mTextViewName;
    TextView mTextViewTime;

    BusinessViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextViewName = itemView.findViewById(R.id.item_view_holder_text_view_name);
        mTextViewTime = itemView.findViewById(R.id.item_view_holder_text_view_time);
    }
}
