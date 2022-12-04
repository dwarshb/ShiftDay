package com.dwarshb.shiftday

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchOpenShiftsAdapter(
    val userType: String,
    private val openShiftsList: ArrayList<ShiftData>
)
    : RecyclerView.Adapter<SearchOpenShiftsAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.cardOpenShiftDate)
        val tvTime: TextView = itemView.findViewById(R.id.cardOpenShiftTime)
        val tvStatus: TextView = itemView.findViewById(R.id.cardOpenShiftStatus)
        val llControls: LinearLayout = itemView.findViewById(R.id.cardOpenShiftControls)
        val mbRequest: MaterialButton = itemView.findViewById(R.id.cardOpenShiftRequest)
        val tvLocation : TextView = itemView.findViewById(R.id.shiftLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_open_shift_item, parent, false)
        return ViewHolder(shiftView)
    }

    // Gets total number of items in the list
    override fun getItemCount(): Int {
        return openShiftsList.size
    }

    fun onItemClick(holder: ViewHolder) {
        holder.llControls.visibility = if(holder.llControls.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Binds data to views in the card item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val openShift = openShiftsList[position]

        val date = openShift.date
        val startTime = openShift.startTime
        val endTime = openShift.endTime
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        val strShiftTime: String = "$strStartTime - $strEndTime"

        var strStatus: String = "Unassigned Shift"
        if(openShift.isAvailable == true && openShift.user != null) {
            strStatus = "Dropped by ${openShift.user?.name}"
        }

        holder.tvLocation.text = openShift.shiftLocation
        holder.tvDate.text = date
        holder.tvTime.text = strShiftTime
        holder.tvStatus.text = strStatus

        holder.itemView.setOnClickListener { onItemClick(holder) }

        // Button to pick up a shift
        holder.mbRequest.setOnClickListener {v ->
            val context = v.context
            val shiftId = openShiftsList[position].id
            val date = openShiftsList[position].date

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val user = User(id = firebaseUser?.uid,
                name = firebaseUser?.displayName,
                userType = userType)
            val map : MutableMap<String?,Any> = mutableMapOf()
            var shiftDatabase: DatabaseReference
            when (userType) {
                Constants.User.STUDENT -> {
                    shiftDatabase = FirebaseDatabase.getInstance().reference
                        .child(Constants.Database.RequestShifts)
                        .child(date.toString())
                    var shift = PendingShiftData(
                        id = shiftId, date = date, user = user,
                        startTime = startTime, endTime = endTime,
                        isDenied =  false, isApproved = false, isDroppedBy = false,
                        shiftLocation = openShift.shiftLocation
                    )
                    map[shiftId] = shift
                }
                else -> {
                    shiftDatabase = FirebaseDatabase.getInstance().reference
                        .child(Constants.Database.Shifts)
                        .child(date.toString())
                    var shift = ShiftData(
                        id = shiftId, date = date, user = user,
                        startTime = startTime, endTime = endTime, isAvailable = false,
                        shiftLocation = openShift.shiftLocation
                    )
                    map[shiftId] = shift

                }
            }
            shiftDatabase.updateChildren(map).addOnSuccessListener {
                when(userType) {
                    Constants.User.STUDENT ->
                        Snackbar.make(holder.itemView,
                            context.getString(R.string.shift_requested),
                            Snackbar.LENGTH_LONG).show()
                    else ->
                        Snackbar.make(holder.itemView,
                            context.getString(R.string.shift_added_success),
                            Snackbar.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Snackbar.make(holder.itemView,
                    String.format(context.getString(R.string.error),it.message),
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }
}