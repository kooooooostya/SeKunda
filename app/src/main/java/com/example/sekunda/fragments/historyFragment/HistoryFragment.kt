package com.example.sekunda.fragments.historyFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.sekunda.R

class HistoryFragment : Fragment() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var mDayPagerAdapter: DayPagerAdapter

    private val presenter = HistoryPresenter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)
        mDayPagerAdapter = DayPagerAdapter(presenter)
        mViewPager = root.findViewById(R.id.history_pager)
        mViewPager.adapter = mDayPagerAdapter

        return root
    }
}