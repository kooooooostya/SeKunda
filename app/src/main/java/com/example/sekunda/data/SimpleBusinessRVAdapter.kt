package com.example.sekunda.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.R

class SimpleBusinessRVAdapter(private val businessArray: ArrayList<Business>) : RecyclerView.Adapter<BusinessRVAdapter.BusinessViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessRVAdapter.BusinessViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_item, parent, false)
        return BusinessRVAdapter.BusinessViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BusinessRVAdapter.BusinessViewHolder, position: Int) {
        holder.mTextViewTime.text = businessArray[position].time
        holder.mTextViewName.text = businessArray[position].name
    }

    override fun getItemCount(): Int {
        return businessArray.size
    }

    fun getItem(index: Int): Business {
        return businessArray[index]
    }
}