package com.dwarshb.shiftday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

// Adapter to display a list of pending shift requests
class PendingShiftsAdapter(private val context: Context,
    private val pendingShiftsList: List<PendingShiftData>)
    : RecyclerView.Adapter<PendingShiftsAdapter.ViewHolder>()
{

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.cardPendingShiftDate)
        val tvTime: TextView = itemView.findViewById(R.id.cardPendingShiftTime)
        val tvAssignment: TextView = itemView.findViewById(R.id.cardPendingShiftAssignment)
        val tvStatus: TextView = itemView.findViewById(R.id.cardPendingShiftStatusValue)
        val approveShift : MaterialButton = itemView.findViewById(R.id.shiftRequestApprove)
        val denyShift : MaterialButton = itemView.findViewById(R.id.shiftRequestDeny)
        val shiftButtonLayout : LinearLayout = itemView.findViewById(R.id.shiftButtonLayout)
    }
    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingShiftsAdapter.ViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_pending_shift_request_item, parent, false)
        return ViewHolder(shiftView)
    }

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return pendingShiftsList.size
    }

    // Binds the data in the views in the card item
    override fun onBindViewHolder(holder: PendingShiftsAdapter.ViewHolder, position: Int) {
        val pendingShift = pendingShiftsList.get(position)
        val shiftId = pendingShift.id

        val strDate = pendingShift.date

        val startTime = pendingShift.startTime
        val endTime = pendingShift.endTime
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        val strShiftTime: String = "$strStartTime. - $strEndTime"

        val timeDifference = endTime?.minus(startTime!!)
        val strHours: String = timeDifference.toString() + " hours"

        var strAssignment: String = "Unassigned \n Requested by: ${pendingShift.user?.name}"
        if(pendingShift.isDroppedBy == true && pendingShift.user != null) {
            strAssignment = "Dropped by ${pendingShift.user?.name}"
        }

        val context = holder.itemView.context
        val statusText: String
        if(pendingShift.isApproved==true) {
            statusText = "Approved"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_green_light)
            )
        }
        else if(pendingShift.isDenied==true) {
            statusText = "Denied"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_red_light)
            )
        }
        else {
            statusText = "Pending"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, android.R.color.background_light)
            )
        }

        holder.tvDate.text = strDate
        holder.tvTime.text = strShiftTime
        holder.tvAssignment.text = strAssignment
        holder.tvStatus.text = statusText
        holder.itemView.setOnClickListener {
            if (holder.shiftButtonLayout.visibility==View.VISIBLE)
                holder.shiftButtonLayout.visibility = View.GONE
            else
                holder.shiftButtonLayout.visibility = View.VISIBLE
        }

        holder.approveShift.setOnClickListener {
            updateShift(holder.itemView,position,pendingShift,Constants.Shift.APPROVE)
        }

        holder.denyShift.setOnClickListener {
            updateShift(holder.itemView,position,pendingShift,Constants.Shift.DENY)
        }
    }

    private fun updateShift(view: View,position: Int,shift : PendingShiftData,request: String) {
        val requestShiftDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.RequestShifts)
        if (request == Constants.Shift.APPROVE) {
            val shiftDatabase = FirebaseDatabase.getInstance().reference
                .child(Constants.Database.Shifts).child(shift.date!!)
            val map = HashMap<String, Any>()
            val updatedShift = ShiftData(
                id = shift.id,
                user = shift.user,
                date = shift.date,
                startTime = shift.startTime,
                endTime = shift.endTime,
                isAvailable = false
            )
            map[shift.id!!] = updatedShift
            shiftDatabase.updateChildren(map).addOnSuccessListener {
                requestShiftDatabase
                    .child(shift.date!!)
                    .child(shift.id!!)
                    .removeValue()
                    .addOnSuccessListener {
                        success(updatedShift, view, position)
                    }
                    .addOnFailureListener {
                        Snackbar.make(
                            view,
                            String.format(context.getString(R.string.error), it.message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }.addOnFailureListener {
                Snackbar.make(
                    view,
                    String.format(context.getString(R.string.error), it.message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            val map = HashMap<String, Any>()
            val updatedShift = PendingShiftData(
                id = shift.id,
                user = shift.user,
                date = shift.date,
                startTime = shift.startTime,
                endTime = shift.endTime,
                isDenied = true,
                isApproved = false,
                isDroppedBy = false
            )
            map[Constants.Database.Shifts] = updatedShift

            requestShiftDatabase.updateChildren(map).addOnSuccessListener {
                success(shift,view,position)
            }.addOnFailureListener {
                Snackbar.make(view,
                    String.format(context.getString(R.string.error),it.message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun success(shiftData: Any,view: View,position: Int) {
        //TODO(Add Undo option)
        Snackbar.make(view,
            context.getString(R.string.update_shift_success),
            Snackbar.LENGTH_LONG).show()
        notifyItemRemoved(position)
    }
}