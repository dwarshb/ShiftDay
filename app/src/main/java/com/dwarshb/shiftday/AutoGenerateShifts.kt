package com.dwarshb.shiftday

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.applandeo.materialcalendarview.CalendarView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class AutoGenerateShifts : AppCompatActivity() {
    val timeFormat: String = "hh:mm a"

    var startHour : Int = 0
    var startMinute : Int = 0
    var endHour : Int = 0
    var endMinute : Int = 0

    var userMap = HashMap<String,User>()

    var random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_generate_shifts)

        val context = this

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val employeeGroup = findViewById<ChipGroup>(R.id.workersGroup)
        val shiftsStartTime = findViewById<EditText>(R.id.shiftBeginTime)
        val shiftsEndTime = findViewById<EditText>(R.id.shiftEndTime)
        val shiftHours = findViewById<EditText>(R.id.shiftHours)
        val shiftLocation = findViewById<EditText>(R.id.shiftLocationEt)
        val generateShift = findViewById<MaterialButton>(R.id.autoGenerateShift)

        calendarView.setDate(Date(System.currentTimeMillis()))
        shiftsStartTime.asTimePicker(context, timeFormat){ hour,minute->
            startHour = hour
            startMinute = minute
        }
        shiftsEndTime.asTimePicker(context, timeFormat){ hour,minute->
            endHour = hour
            endMinute = minute
        }

        generateShift.setOnClickListener {
            if (shiftLocation.text.isEmpty()) {
                shiftLocation.error = getString(R.string.fill_this_field)
                return@setOnClickListener
            } else if (shiftsStartTime.text.isEmpty()) {
                shiftsStartTime.error = getString(R.string.fill_this_field)
                return@setOnClickListener
            } else if (shiftsEndTime.text.isEmpty()) {
                shiftsEndTime.error = getString(R.string.fill_this_field)
                return@setOnClickListener
            }


            for (calendar in calendarView.selectedDates) {
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                var date = Utils.dateFormat.format(Date(calendar.timeInMillis))
                calendar.set(year,month,day,startHour,startMinute)
                var startTime = calendar.time
                calendar.set(year,month,day,endHour,endMinute)
                var endTime = calendar.time

                val perShiftHour = shiftHours.text.toString().toInt()
                for (hour in startHour..endHour step perShiftHour) {
                    calendar.set(year,month,day,hour,endMinute)
                    var endShiftTime = calendar.time
                    val shiftId = System.currentTimeMillis()
                    var randomUser = userMap.entries.elementAt(random.nextInt(userMap.size))
                    var user : User? = randomUser.value
                    if (calendarView.selectedDates.size<=7 &&
                        user?.hoursWorkedAWeek!! >20) {
                        user = null
                    } else {
                        userMap[user?.id]?.hoursWorkedAWeek =
                            userMap[user?.id]?.hoursWorkedAWeek?.plus(perShiftHour)!!
                    }
                    val isAvailable: Boolean = false
                    val shiftObject = when (user) {
                        null -> {
                            ShiftData(
                                shiftId.toString(),
                                null,
                                date,
                                startTime.time.toDouble(),
                                endShiftTime.time.toDouble(),
                                isAvailable,
                                shiftLocation.text.toString()
                            )
                        }
                        else -> {
                            ShiftData(
                                shiftId.toString(),
                                user,
                                date,
                                startTime.time.toDouble(),
                                endShiftTime.time.toDouble(),
                                isAvailable,
                                shiftLocation.text.toString()
                            )
                        }
                    }
                    Log.e("onShiftCreate: ", "$shiftObject")
                }
            }
        }
        val studentDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.Users)
            .orderByChild(Constants.User.Type)
            .equalTo(Constants.User.STUDENT)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                val chip = Chip(context)
                chip.chipBackgroundColor =
                    ColorStateList.valueOf(context.getColor(R.color.color_primary))
                chip.setTextColor(getColor(R.color.white))
                chip.text = user?.name
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    userMap.remove(user?.id)
                }
                userMap[user?.id!!] = user
                employeeGroup.addView(chip)
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
    }
}