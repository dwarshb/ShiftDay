package com.dwarshb.shiftday

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val myDataset: Array<ShiftData>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val date = cardView.findViewById<TextView>(R.id.cardScheduleDateHours)
        val shiftTime = cardView.findViewById<TextView>(R.id.cardScheduleShiftTime)
        //val hours = cardView.findViewById<TextView>(R.id.cardScheduleHours)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_assigned_shift_item, parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(cardView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.date.text = "${myDataset[position].date}"
        holder.shiftTime.text = "" + myDataset.get(position).startTime + " - " + myDataset.get(position).endTime
        //holder.hours.text = "Hello"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}