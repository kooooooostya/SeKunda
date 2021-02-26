package com.example.sekunda.fragments.secFragment

import android.app.*
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sekunda.Data.Business
import com.example.sekunda.MainActivity.Companion.newIntent
import com.example.sekunda.R
import com.example.sekunda.fragments.BaseFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_sec.*
import moxy.presenter.InjectPresenter

class SecFragment : BaseFragment(), SecView {

    @InjectPresenter
    lateinit var presenter: SecPresenter

    lateinit var recyclerAdapter: BusinessRecyclerAdapter


    override fun initialize() {

        recyclerAdapter = BusinessRecyclerAdapter(presenter.getBusinessList())
        //mIndexCurrentBusiness = mRecyclerAdapter.findIndex(mCurrentBusiness)

        secRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        secRecyclerView.adapter = recyclerAdapter

        secButtonNew.setOnClickListener {
            presenter.startOrStopTimer()
        }
        itemTouchHelper.attachToRecyclerView(secRecyclerView)
    }

    override fun getContentView(): Int {
        return R.layout.fragment_sec
    }

    private fun printInfo(business: Business?) {
        secTextViewName.text = business!!.name
        secTextViewTimer.text = business.time
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID.toString(), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Creates notification
    private fun createNotification() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID.toString())
        val pendingIntent = PendingIntent.getActivity(requireContext(),
                0, newIntent(requireContext()), 0)

        //TODO change icon
        builder.setContentText(getString(R.string.channel_description))
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentTitle(getString(R.string.app_name)).priority = Notification.PRIORITY_DEFAULT
        val notificationManager = (requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.notify(CHANNEL_ID, builder.build())
    }

    //Cancel Notification
    private fun cancelNotification() {
        val notificationManager = (requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.cancel(CHANNEL_ID)
    }

    companion object {
        private const val CHANNEL_ID = 152
    }

    override fun updateTimer(time: String) {
        secTextViewTimer.text = time
    }

    override fun showInputDialog() {
        val builder = AlertDialog.Builder(context)
        val inputDialog = LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog, null, false)
        builder.setTitle("Write name of task")
        builder.setView(inputDialog)
        val editTextName = inputDialog.findViewById<EditText>(R.id.input_dialog_editText)

        builder.setPositiveButton(R.string.button_ok) { _, _ ->
            if (editTextName.text.isNotEmpty()) {
                val business = Business(editTextName.text.toString(), 0)
                startTimer(business)
            }
        }
        builder.create().show()
    }

    override fun stopTimer(business: Business, businessIndex: Int) {
        //recyclerAdapter.changeItem(business, businessIndex)
        recyclerAdapter.notifyItemChanged(businessIndex)
        secTextViewTimer.setText(R.string.time_to_do)
        secTextViewName.text = ""
        secButtonNew.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        cancelNotification()
    }

    override fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun startTimer(business: Business, isResume: Boolean = false) {
        secTextViewName.text = business.name
        secButtonNew.setImageResource(R.drawable.ic_stop_black_24dp)
        createNotification()
        if (!isResume){
            presenter.startTimer(business)
        }
    }


    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.LEFT ->{
                    startTimer(recyclerAdapter.getItem(viewHolder.adapterPosition), true)
                    presenter.resumeBusiness(recyclerAdapter.getItem(viewHolder.adapterPosition))
                }
                ItemTouchHelper.RIGHT -> {
                    val builder = AlertDialog.Builder(requireContext())
                    val inputDialog = LayoutInflater.from(requireContext()).inflate(R.layout.input_dialog, null, false)
                    val editTextName = inputDialog.findViewById<EditText>(R.id.input_dialog_editText)
                    builder.setTitle("Rename task")
                    builder.setView(inputDialog)

                    val oldBusiness = recyclerAdapter.getItem(viewHolder.adapterPosition)
                    editTextName.setText(oldBusiness.name)

                    builder.setPositiveButton(R.string.button_ok) { _, _ ->
                        val newBusiness = oldBusiness.clone() as Business
                        newBusiness.name = editTextName.text.toString()
                        presenter.renameBusiness(newBusiness, oldBusiness)
                        recyclerAdapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                    builder.create().show()
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight))
                    .addSwipeLeftActionIcon(R.drawable.ic_play_arrow_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                    .addSwipeRightActionIcon(R.drawable.ic_mode_edit_black_24dp)
                    .create()
                    .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    })
}