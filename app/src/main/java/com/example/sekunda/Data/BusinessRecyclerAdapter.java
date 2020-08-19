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

    BusinessRecyclerAdapter(ArrayList<Business> arrayList) {
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

    public int findIndex(Business business){
        for (int i = 0; i < mBusinessArrayList.size(); i ++){
            if(business.getName().equals(mBusinessArrayList.get(i).getName())) return i;
        }
        return -1;
    }

    // if incomplete task does not exist returns new Business
    public Business findIncompleteTask(){
        for (Business business : mBusinessArrayList){
            if(!business.isComplete()) return business;
        }
        return new Business("", 0);
    }

    // check name is free
    private boolean checkNameIsFree(Business business){
        for (Business business1 : mBusinessArrayList){
            if(business.getName().equals(business1.getName())) return false;
        }
        return true;
    }

    // check name is free, if not free, make free and and to db
    public Business insertBusiness(Business business){
        if (!checkNameIsFree(business)){
            business.setName(business.getName() + "1");
            return insertBusiness(business);
        }else {
            mBusinessArrayList.add(0, business);
            this.notifyItemInserted(0);
            mSQLiteOpenHelper.insertBusinessAsync(business);
            return business;
        }
    }
    public void changeBusiness(Business newBusiness, int index){
        mSQLiteOpenHelper.changeAsync(newBusiness, mBusinessArrayList.get(index));
        mBusinessArrayList.set(index, newBusiness);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mSQLiteOpenHelper.close();
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
