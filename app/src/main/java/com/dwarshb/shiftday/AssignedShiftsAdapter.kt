package com.dwarshb.shiftday

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

import java.text.SimpleDateFormat

// Adapter to show a list of assigned shifts
class AssignedShiftsAdapter(
    private val assignedShiftList: List<ShiftData>)
    : RecyclerView.Adapter<AssignedShiftsAdapter.ViewHolder>()
{
    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView)
    {
        val tvDateHours = cardView.findViewById<TextView>(R.id.cardScheduleDateHours)
        val tvShiftTime = cardView.findViewById<TextView>(R.id.cardScheduleShiftTime)
        val llButtons = cardView.findViewById<LinearLayout>(R.id.cardScheduleButtonLayout)
        val mbDropBtn = cardView.findViewById<MaterialButton>(R.id.cardScheduleDrop)
    }

    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_assigned_shift_item, parent, false)
            as CardView

        return ViewHolder(cardView)
    }

    // Binds the values to the views within the card
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shift = assignedShiftList.get(position)
        Log.e("onBindViewHolder: ",shift.toString())
        val date = shift.date

        val startTime = shift.startTime
        val endTime = shift.endTime
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        val strShiftTime: String = "$strStartTime. - $strEndTime"

        val timeDifference = endTime?.minus(startTime!!)
        val strHours: String = timeDifference.toString() + " hours"


        holder.tvDateHours.text = date
        holder.tvShiftTime.text = strShiftTime

        val context = holder.itemView.context
        if(shift.isAvailable == true) {
            holder.mbDropBtn.text = "Cancel Drop Request"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.teal_200))
        } else {
            holder.mbDropBtn.text = "Drop Shift"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.white))
        }

        holder.itemView.setOnClickListener {
            onItemClickHandler(holder, position)
        }

        holder.mbDropBtn.setOnClickListener { v ->
            onDropShiftClicked(v.context, shift, holder, position)
        }
    }

    // Gets the total number of items in the array
    override fun getItemCount(): Int {
        return assignedShiftList.size
    }

    // Performs action if an item is clicked
    private fun onItemClickHandler(holder: ViewHolder, position: Int) {
        holder.llButtons.visibility =
            if(holder.llButtons.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    // Performs action if a shift is dropped
    private fun onDropShiftClicked(
        context: Context,
        shift: ShiftData,
        holder: ViewHolder,
        position: Int
    ) {
        val currentShift = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.Shifts).child(shift?.date!!)
        val dropDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.RequestShifts)
    }
}