package com.example.sekunda.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.sekunda.Data.DayPagerAdapter
import com.example.sekunda.R

class HistoryFragment : Fragment() {
    private lateinit var mViewPager: ViewPager
    private lateinit var mDayPagerAdapter: DayPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)

        mDayPagerAdapter = DayPagerAdapter(requireContext())
        mViewPager = root.findViewById(R.id.history_pager)
        mViewPager.adapter = mDayPagerAdapter
        return root
    }
}