package com.example.sekunda.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.R

class BusinessRVAdapter(private val dataProvider: BusinessListProvider) :
        RecyclerView.Adapter<BusinessRVAdapter.BusinessViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_item, parent, false)
        return BusinessViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        if (dataProvider.getBusinessList()[position].isRunning){
            holder.mTextViewTime.text = Business.RUNNING
        }else{
            holder.mTextViewTime.text = dataProvider.getBusinessList()[position].time
        }
        holder.mTextViewName.text = dataProvider.getBusinessList()[position].name
    }

    override fun getItemCount(): Int {
        return dataProvider.getBusinessList().size
    }

    fun getItem(index: Int): Business{
        return dataProvider.getBusinessList()[index]
    }

    class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextViewName: TextView = itemView.findViewById(R.id.item_view_holder_text_view_name)
        var mTextViewTime: TextView = itemView.findViewById(R.id.item_view_holder_text_view_time)
    }
}

