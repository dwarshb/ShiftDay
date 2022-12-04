package com.dwarshb.shiftday

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchOpenShiftsActivity: AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var listOfAvailableShift = ArrayList<ShiftData>()
    private lateinit var userType : String
    private lateinit var noOpenShiftView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_open_shifts)

        userType = intent.getStringExtra(Constants.User.Type).toString()
        recyclerView = findViewById(R.id.availableShiftsRecyclerView)
        viewAdapter = SearchOpenShiftsAdapter(userType = userType,listOfAvailableShift)
        recyclerView.adapter = viewAdapter
        calendarView = findViewById(R.id.calenderView)
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        noOpenShiftView = findViewById(R.id.no_open_shifts_message)
        fetchAvailableShift(year,month+1,day)
        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            fetchAvailableShift(year,month+1,day)
        }

        // Back arrow button to go to home activity
        findViewById<ImageView>(R.id.backArrowButtonSearchOpen).setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchAvailableShift(year : Int, month: Int,day : Int) {
        var date = "$year-$month-$day"
        val formattedDate = Utils.formatDateToWeek(date,"yyyy-MM-dd","MMM dd E")

        val shiftsDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.Shifts)
            .child(formattedDate)
        Log.e("fetchAvailableShift: ",date)
        listOfAvailableShift.clear()
        viewAdapter.notifyDataSetChanged()
        noOpenShiftView.visibility = View.VISIBLE
        val childListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                noOpenShiftView.visibility = View.GONE
                val shift = snapshot.getValue(ShiftData::class.java)
                if (shift?.isAvailable!!)
                    listOfAvailableShift.add(shift)
                viewAdapter.notifyItemInserted(viewAdapter.itemCount-1)
                Log.e( "onChildAdded: ",listOfAvailableShift.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                noOpenShiftView.visibility = View.GONE
                val shift = snapshot.getValue(ShiftData::class.java)
                if (!shift?.isAvailable!!)
                    listOfAvailableShift.remove(shift)
                viewAdapter.notifyDataSetChanged()
                Log.e( "onChildChanged: ",listOfAvailableShift.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.e( "onChildRemoved: ",snapshot.toString()+" ")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("onChildMoved: ",snapshot.toString()+" ")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled: ",error.message)
            }
        }
        shiftsDatabase.addChildEventListener(childListener)
    }
}