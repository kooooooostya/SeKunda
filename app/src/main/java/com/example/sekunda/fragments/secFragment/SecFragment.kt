package com.example.sekunda.fragments.secFragment

import android.app.AlertDialog
import android.graphics.Canvas
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.R
import com.example.sekunda.adapters.BusinessRVAdapter
import com.example.sekunda.data.Business
import com.example.sekunda.fragments.BaseFragment
import com.example.sekunda.tools.getLongFromSharedPref
import com.example.sekunda.tools.isContainedInSharedPref
import com.example.sekunda.tools.removeFromSharedPref
import com.example.sekunda.tools.setLongToSharedPref
import com.example.sekunda.views.HowMuchWorkedDialog
import com.google.android.material.textfield.TextInputEditText
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_sec.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.presenter.InjectPresenter
import java.util.*

class SecFragment : BaseFragment(), SecView {

    @InjectPresenter
    lateinit var presenter: SecPresenter

    lateinit var mRVAdapter: BusinessRVAdapter
    lateinit var howMuchWorkedDialog: HowMuchWorkedDialog

    private val prefTimeKey = "pref_time"
    private val prefIndexKey = "pref_name"

    override fun initialize() {
        mRVAdapter = BusinessRVAdapter(presenter)

        howMuchWorkedDialog = HowMuchWorkedDialog(requireActivity())

        secRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        secRecyclerView.adapter = mRVAdapter

        secButtonNew.setOnClickListener {
            presenter.startOrStopTimer()
        }
        itemTouchHelper.attachToRecyclerView(secRecyclerView)

        checkNotFinished()
    }

    private fun checkNotFinished() {
        if (isContainedInSharedPref(prefTimeKey)) {
            val time = getLongFromSharedPref(prefTimeKey, -1)
            val index = getLongFromSharedPref(prefIndexKey, -1)

            val callback = { millis: Long ->
                if (time.toInt() != -1 && index.toInt() != -1) {
                    presenter.resumeBusiness(millis, index)
                    mRVAdapter.notifyDataSetChanged()
                    secButtonNew.setImageResource(R.drawable.ic_stop_black_24dp)
                }
            }

            howMuchWorkedDialog.showDialog(time, callback)
        }

    }

    private fun writeNotFinished() {
        if (presenter.isCurrentBusinessRunning()) {
            setLongToSharedPref(prefTimeKey, Date().time - presenter.getCurrentBusinessMillis())
            setLongToSharedPref(prefIndexKey, presenter.getCurrentBusinessDbIndex())
        }
    }

    private fun removeNotFinished() {
        removeFromSharedPref(prefTimeKey)
        removeFromSharedPref(prefIndexKey)
    }

    override fun getContentView(): Int {
        return R.layout.fragment_sec
    }

    override fun updateTimer(time: String) {
        secTextViewTimer.text = time
    }

    override fun showInputDialog() {
        val builder = AlertDialog.Builder(context)
        val inputDialog =
            LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog, null, false)
        builder.setTitle("Write name of task")
        builder.setView(inputDialog)
        val editTextName = inputDialog.findViewById<TextInputEditText>(R.id.input_dialog_text_field)

        builder.setPositiveButton(R.string.button_ok) { _, _ ->
            if (editTextName.text?.isNotEmpty() == true) {
                val business = Business(editTextName.text.toString(), 0)
                startTimer(business)
            }
        }
        builder.create().show()
    }

    override fun stopTimer(business: Business, businessIndex: Int) {
        mRVAdapter.notifyItemChanged(businessIndex)
        secTextViewTimer.setText(R.string.time_to_do)
        secTextViewName.text = ""
        secButtonNew.setImageResource(R.drawable.ic_play_arrow_black_24dp)

        GlobalScope.launch(Dispatchers.IO) { removeNotFinished() }
    }

    override fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun updateRecyclerView() {
        mRVAdapter.notifyDataSetChanged()
    }

    override fun notifyItemInserted(index: Int) {
        mRVAdapter.notifyItemInserted(index)
    }

    private fun startTimer(business: Business, isResume: Boolean = false) {
        secTextViewName.text = business.name
        secButtonNew.setImageResource(R.drawable.ic_stop_black_24dp)
        if (!isResume) {
            presenter.startTimer(business)
        }
        GlobalScope.launch(Dispatchers.Main) { writeNotFinished() }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
    ) {

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    startTimer(mRVAdapter.getItem(viewHolder.adapterPosition), true)
                    presenter.resumeBusiness(mRVAdapter.getItem(viewHolder.adapterPosition))

                    mRVAdapter.notifyItemChanged(viewHolder.layoutPosition)
                }
                ItemTouchHelper.RIGHT -> {
                    val builder = AlertDialog.Builder(requireContext())
                    val inputDialog = LayoutInflater.from(requireContext())
                        .inflate(R.layout.input_dialog, null, false)
                    val editTextName =
                        inputDialog.findViewById<EditText>(R.id.input_dialog_text_field)
                    builder.setTitle("Rename task")
                    builder.setView(inputDialog)

                    val oldBusiness = mRVAdapter.getItem(viewHolder.adapterPosition)
                    editTextName.setText(oldBusiness.name)

                    builder.setPositiveButton(R.string.button_ok) { _, _ ->
                        presenter.renameBusiness(oldBusiness, editTextName.text.toString())
                        mRVAdapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                    builder.create().show()
                }
            }
        }

        override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeLeftBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryLight
                    )
                )
                .addSwipeLeftActionIcon(R.drawable.ic_play_arrow_black_24dp)
                .addSwipeRightBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                    )
                )
                .addSwipeRightActionIcon(R.drawable.ic_mode_edit_black_24dp)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    })


}