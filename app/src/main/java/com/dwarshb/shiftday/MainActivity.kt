package com.dwarshb.shiftday

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: AssignedShiftsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var noShiftLayout: LinearLayout
    lateinit var prefs: SharedPreferences
    lateinit var user: FirebaseUser
    lateinit var context: Context
    var name: String? = null
    var type: String? = null
    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = this.getSharedPreferences(packageName.toString(), Context.MODE_PRIVATE)
        context = this
        viewManager = LinearLayoutManager(this)

        noShiftLayout = findViewById(R.id.no_shifts_layout)
        recyclerView = findViewById(R.id.showShiftsRecyclerView)

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)

        // Shows the navigation drawer
        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)
        val headerView = navigationViewItems.getHeaderView(0)
        val loggedInUserName = headerView.findViewById<TextView>(R.id.loggedInUsername)
        user = FirebaseAuth.getInstance().currentUser!!
        loggedInUserName.text = user.displayName

        type = intent.getStringExtra(Constants.User.Type)
        fetchScheduledShifts()
        if (viewAdapter.itemCount<1) {
            noShiftLayout.visibility = View.VISIBLE
        } else {
            noShiftLayout.visibility = View.GONE
        }

        // Disables the manager controls button if the user is an student
        if (type.equals(Constants.User.STUDENT,ignoreCase = true)) {
            navigationViewItems.menu.removeItem(R.id.managerControlsButton)
        }

        // Items in the navigation view
        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {

                // Sends users to manager controls
                R.id.managerControlsButton -> {
                    if (type.equals(Constants.User.MANAGER,ignoreCase = true)) {
                        val intentToManagerControlsActivity = Intent(this@MainActivity, ManagerControlsActivity::class.java)
                        intentToManagerControlsActivity.putExtra(Constants.User.Id, id)
                        intentToManagerControlsActivity.putExtra(Constants.User.Type,type)
                        startActivity(intentToManagerControlsActivity)
                    }
                    else {
                        Toast.makeText(this@MainActivity,
                            "Manager controls not available for employees",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                // Displays the current schedule
                R.id.myScheduleButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }

                // Sends user to request time off screen
                R.id.requestTimeOffButton -> {
//                    val intentToListOfTimeOffRequestsActivity = Intent(context, ListOfTimeOffRequestsActivity::class.java)
//                    intentToListOfTimeOffRequestsActivity.putExtra("tokenCode", tokenCode)
//                    intentToListOfTimeOffRequestsActivity.putExtra("accessCode", accessCode)
//                    startActivity(intentToListOfTimeOffRequestsActivity)
                }

                // Sends user to search open shift screen
                R.id.searchOpenShiftsButton -> {
                    val intentToOpenShiftsActivity = Intent(context, SearchOpenShiftsActivity::class.java)
                    intentToOpenShiftsActivity.putExtra(Constants.User.Type,type)
//                    intentToOpenShiftsActivity.putExtra("tokenCode", tokenCode)
//                    intentToOpenShiftsActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToOpenShiftsActivity)
                }

                // Display pending shifts screen
                R.id.pendingShiftsButton -> {
                    val intentToPendingShiftRequestActivity = Intent(context, PendingShiftRequestActivity::class.java)
                    startActivity(intentToPendingShiftRequestActivity)
                }

                // Button to log out
                R.id.logOutButtonMenu -> {
                    with(prefs.edit()) {
                        putBoolean(getString(R.string.IS_LOGGED_OUT), true)
                        apply()
                    }
                    FirebaseAuth.getInstance().signOut()
                    val intentToHome = Intent(this@MainActivity, SplashActivity::class.java)
                    startActivity(intentToHome)
                }
            }
            true
        }
    }

    private fun fetchScheduledShifts() {
        val scheduledShifts = ArrayList<ShiftData>()
        viewAdapter = AssignedShiftsAdapter(scheduledShifts)
        recyclerView.adapter = viewAdapter
        val shiftDatabase = FirebaseDatabase.getInstance().reference.child(Constants.Database.Shifts)
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                for (shiftSnapshot in snapshot.children) {
                    val shift = shiftSnapshot.getValue(ShiftData::class.java)
                    if(shift?.user?.id.equals(user.uid)) {
                        noShiftLayout.visibility = View.GONE
                        scheduledShifts.add(shift!!)
                        viewAdapter.notifyItemInserted(viewAdapter.itemCount - 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.d( "onChildChanged: ",shift.toString())
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.d( "onChildMoved: ",shift.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val shift = snapshot.getValue(ShiftData::class.java)
                Log.e("onChildRemoved: ",shift.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        shiftDatabase.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        fetchScheduledShifts()
    }
}