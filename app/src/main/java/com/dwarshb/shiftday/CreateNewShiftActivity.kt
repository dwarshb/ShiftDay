package com.dwarshb.shiftday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CreateNewShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "hh:mm a"

    var year : Int = 0
    var month : Int = 0
    var day : Int = 0
    var startHour : Int = 0
    var startMinute : Int = 0
    var endHour : Int = 0
    var endMinute : Int = 0

    var userMap: HashMap<String, User?> = HashMap()
    var userNames: ArrayList<String> = ArrayList<String>()
    var selectUser: String = "None"

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_shift)

        val context = this


        // Back button to go back to the manager controls activity
        findViewById<ImageView>(R.id.backArrowButtonCreateNewShift).setOnClickListener {
            onBackPressed()
        }

        // Widgets
        val calView: CalendarView = findViewById<CalendarView>(R.id.calrenderViewSelectDate)
        val shift_begin: EditText = findViewById<EditText>(R.id.shiftBeginTime)
        val shift_end: EditText = findViewById<EditText>(R.id.shiftEndTime)
        val employeeSpinner: Spinner = findViewById(R.id.selectEmployeeSpinnerInNewShift)
        val btnCreateShift: Button = findViewById<Button>(R.id.createNewShiftButtonInCreateNewShift)

        var cal = Calendar.getInstance()
        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            this.year = year
            this.month = month
            this.day = dayOfMonth

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            calView.date = cal.timeInMillis
        }

        // Transform EditText to TimePicker
        shift_begin.asTimePicker(context, timeFormat){ hour,minute->
            startHour = hour
            startMinute = minute
        }
        shift_end.asTimePicker(context, timeFormat){ hour,minute->
            endHour = hour
            endMinute = minute
        }

        userNames.add("None")
        userMap["None"] = null

        val spinnerAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            userNames
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        employeeSpinner.adapter = spinnerAdapter
        employeeSpinner.onItemSelectedListener = context

        val studentDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.Users)
            .orderByChild(Constants.User.Type)
            .equalTo(Constants.User.STUDENT)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                userMap[user?.name!!] = user
                userNames.add(user.name!!)
                spinnerAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled: ",error.message )
            }
        }
        studentDatabase.addChildEventListener(childEventListener)

        // Creates a shift button
        btnCreateShift.setOnClickListener{
            val shiftId = System.currentTimeMillis()
            val user = userMap[selectUser]?:null
            val isAvailable: Boolean = user == null // If none is selected make it an open shift
            var date = dateFormat.format(Date(calView.date))
            cal.set(year,month,day,startHour,startMinute)
            var startTime = cal.time
            cal.set(year,month,day,endHour,endMinute)
            var endTime = cal.time
            Log.e("onShiftCreate: ", "${startTime}\n ${endTime}")

            val shiftObject = when(user) {
                null->{
                    ShiftData(
                        shiftId.toString(), null,date,
                        startTime.time.toDouble(), endTime.time.toDouble(), isAvailable
                    )
                }
                else-> {
                    ShiftData(
                        shiftId.toString(), user!!, date,
                        startTime.time.toDouble(), endTime.time.toDouble(), isAvailable
                    )
                }
            }

            // API request to send a create new shift request
            val shiftDatabase = FirebaseDatabase.getInstance().reference
                .child(Constants.Database.Shifts).child(date)
            val map : MutableMap<String,Any> = mutableMapOf()
            map[shiftId.toString()] = shiftObject
            shiftDatabase.updateChildren(map).addOnSuccessListener {
                Snackbar.make(calView,getString(R.string.shift_added_success),
                    Snackbar.LENGTH_LONG).show()
                onBackPressed()
            }.addOnFailureListener {
                Snackbar.make(calView,String.format(getString(R.string.error),it.message),
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        var list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }


    // Gets the employee selected
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectUser = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
