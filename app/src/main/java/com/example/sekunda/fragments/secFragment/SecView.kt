package com.example.sekunda.fragments.secFragment

import com.example.sekunda.data.Business
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface SecView : MvpView {
    fun updateTimer(time: String)
    fun showInputDialog()
    fun stopTimer(business: Business, businessIndex: Int)
    fun showToast(message: String)
    fun updateRecyclerView()
    fun notifyItemInserted(index: Int)
    fun showError(message: String)
}