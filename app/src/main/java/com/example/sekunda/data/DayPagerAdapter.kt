package com.example.sekunda.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.sekunda.R
import com.example.sekunda.fragments.secFragment.BusinessRecyclerAdapter
import java.util.*

class DayPagerAdapter(private val mContext: Context) : PagerAdapter() {
    private val mDayArrayList: ArrayList<Day>
    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val itemView = inflater.inflate(R.layout.pager_item, container)
        val textViewDay = itemView.findViewById<TextView>(R.id.day_item_tv_day)
        val textViewTotalTime = itemView.findViewById<TextView>(R.id.day_item_tv_all_time)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.day_item_recycler)
        val day = mDayArrayList[position]
        textViewDay.text = day.calendarTime
        textViewTotalTime.text = day.allTime

//        val businessRecyclerAdapter: BusinessRecyclerAdapter = if (day.businessArrayList.size == 0) {
//            val arrayList = ArrayList<Business>()
//            arrayList.add(Business("Nothing Today", 0))
//            //BusinessRecyclerAdapter(arrayList)
//        } else {
//            BusinessRecyclerAdapter(day.businessArrayList)
//        }
        //recyclerView.adapter = businessRecyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        container.addView(itemView)
        return itemView
    }

    private val fulledDayList: ArrayList<Day>
        get() {
            val dayArrayList = ArrayList<Day>()
            val calendar = Calendar.getInstance()
            for (i in 0 until PAGE_COUNT) {
                dayArrayList.add(Day(calendar.clone() as Calendar))
                calendar[Calendar.DATE] = calendar[Calendar.DATE] - 1
            }
            return dayArrayList
        }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    companion object {
        private const val PAGE_COUNT = 7
    }

    init {
        mDayArrayList = fulledDayList
    }
}