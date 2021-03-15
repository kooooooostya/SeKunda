package com.example.sekunda.fragments.secFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.data.Business
import com.example.sekunda.R

class BusinessRecyclerAdapter(private val presenter: SecPresenter) :
        RecyclerView.Adapter<BusinessRecyclerAdapter.BusinessViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_item, parent, false)
        return BusinessViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:BusinessViewHolder, position: Int) {
        holder.mTextViewTime.text = presenter.getBusinessList()[position].time
        holder.mTextViewName.text = presenter.getBusinessList()[position].name
    }

    override fun getItemCount(): Int {
        return presenter.getBusinessList().size
    }

    fun getItem(index: Int): Business{
        return presenter.getBusinessList()[index]
    }

    inner class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextViewName: TextView = itemView.findViewById(R.id.item_view_holder_text_view_name)
        var mTextViewTime: TextView = itemView.findViewById(R.id.item_view_holder_text_view_time)
    }
}

