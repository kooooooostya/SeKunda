package com.example.sekunda.fragments.historyFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.R
import com.example.sekunda.data.Day
import com.example.sekunda.data.SimpleBusinessRVAdapter

class DayPagerAdapter(private var dayList: ArrayList<Day>) : RecyclerView.Adapter<DayPagerAdapter.DayViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pager_item, parent, false)
        return DayViewHolder(itemView)
    }

    fun setDayList(dayList: ArrayList<Day>){
        this.dayList = dayList
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {

        val day = dayList[position]

        holder.textViewDate.text = day.dmyTime
        holder.textViewTotalTime.text = day.hmsTime

        if (day.businessArrayList.size == 0) day.addEmptyBusiness()

        val recyclerAdapter = SimpleBusinessRVAdapter(day.businessArrayList)

        holder.recyclerView.adapter = recyclerAdapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.recyclerView.context)
    }

    companion object {
        private const val PAGE_COUNT = 7
    }

    override fun getItemCount(): Int {
        return PAGE_COUNT
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewDate: TextView = itemView.findViewById(R.id.day_item_tv_day)
        var textViewTotalTime: TextView = itemView.findViewById(R.id.day_item_tv_all_time)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.day_item_recycler)
    }
}