package com.dwarshb.shiftday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class PendingShiftRequestActivity: AppCompatActivity() {

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    lateinit var context: Context
    lateinit var tokenCode: String
    var accessCode: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PendingShiftsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var noShiftLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pending_shift_requests)

        context = this

        viewManager = LinearLayoutManager(this)

        noShiftLayout = findViewById(R.id.no_shifts_layout)
        recyclerView = findViewById(R.id.pendingShiftRequestsRecyclerView)
        // API request to show a list of pending shift requests
        fetchPendingShift()
        if (viewAdapter.itemCount<1) {
            noShiftLayout.visibility = View.VISIBLE
        } else {
            noShiftLayout.visibility = View.GONE
        }
        // Back button to go back to the home activity
        findViewById<ImageView>(R.id.backArrowButtonPendingShiftRequests).setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchPendingShift() {
        val pendingShifts = ArrayList<PendingShiftData>()
        viewAdapter = PendingShiftsAdapter(context,pendingShifts)
        recyclerView.adapter = viewAdapter
        val requestDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.RequestShifts)

        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                for (shiftSnapshot in snapshot.children) {
                    val shift = shiftSnapshot.getValue(PendingShiftData::class.java)
                    noShiftLayout.visibility = View.GONE
                    pendingShifts.add(shift!!)
                    viewAdapter.notifyItemInserted(viewAdapter.itemCount - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.e("onChildChanged: ",shift.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.e("onChildMoved: ",shift.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.e("onChildRemoved: ",shift.toString())
            }
        }
        requestDatabase.addChildEventListener(childEventListener)
    }
}