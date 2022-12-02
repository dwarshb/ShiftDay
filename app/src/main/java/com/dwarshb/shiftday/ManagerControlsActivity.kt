package com.dwarshb.shiftday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ManagerControlsActivity: AppCompatActivity() {
    lateinit var context: Context
    lateinit var generateCodeButton: Button
    lateinit var createNewShiftButton: Button
    lateinit var addRemoveShiftsButton: Button
    lateinit var approveShiftRequestsButton: Button
    lateinit var approveTimeOffRequestButton: Button
    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manager_controls)

        context = this

        // Button to go back to the home activity
        findViewById<ImageView>(R.id.backArrowButton).setOnClickListener {
            onBackPressed()
        }

        generateCodeButton = findViewById(R.id.generateCodeButton)
        createNewShiftButton = findViewById(R.id.createNewShiftButton)
        addRemoveShiftsButton = findViewById(R.id.addRemoveShiftsButton)
        approveShiftRequestsButton = findViewById(R.id.approveShiftRequestsButton)
        approveTimeOffRequestButton = findViewById(R.id.approveTimeOffRequestButton)
        // Button to go to the generate code activity
        generateCodeButton.setOnClickListener {
//            val intentToCompanyCodeActivity = Intent(context, CompanyCodeActivity::class.java)
//            intentToCompanyCodeActivity.putExtra("tokenCode", tokenCode)
//            intentToCompanyCodeActivity.putExtra("accessCode", accessCode)
//            startActivity(intentToCompanyCodeActivity)
        }

        // Button to go to add/remove shift requests activity
        addRemoveShiftsButton.setOnClickListener {
//            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
//            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
//            intentToCurrentShiftAddRemoveActivity.putExtra("accessCode", accessCode)
//            startActivity(intentToCurrentShiftAddRemoveActivity)
        }

        // Button to go to the create new shift activity
        createNewShiftButton.setOnClickListener {
            val intentToCreateNewShiftActivity = Intent(context, CreateNewShiftActivity::class.java)
            startActivity(intentToCreateNewShiftActivity)
        }

        // Button to go to the approve/deny shift requests activity
        approveShiftRequestsButton.setOnClickListener {
//            val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
//            intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
//            intentToApproveShiftRequestActivity.putExtra("accessCode", accessCode)
//            startActivity(intentToApproveShiftRequestActivity)
        }

        // Button to go to approve/deny time off request activity
        approveTimeOffRequestButton.setOnClickListener {
//            val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
//            intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
//            intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
//            startActivity(intentToApproveTimeOffRequestActivity)
        }
    }

}
