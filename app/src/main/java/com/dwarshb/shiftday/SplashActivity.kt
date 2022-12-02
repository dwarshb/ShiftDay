package com.dwarshb.shiftday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class SplashActivity : AppCompatActivity() {

    // below is the list which we have created in which
    // we can add the authentication which we have to
    // display inside our app.
    var providers: List<IdpConfig> = Arrays.asList( // below is the line for adding
        // email and password authentication.
        EmailBuilder().build()  // below line is used for adding google
        // authentication builder in our app.
//        GoogleBuilder().build(),  // below line is used for adding phone
        // authentication builder in our app.
 //       PhoneBuilder().build()
    )
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    var firebaseUser: FirebaseUser? = null
    var view : ImageView? = null
    var userType : String? = Constants.User.STUDENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        view = findViewById<ImageView>(R.id.splash_screen_logo)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null) {
            checkIfUserIsInDatabase()
        } else {
            signInToFirebase()
        }


    }

    private fun checkIfUserIsInDatabase() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User info and use the values to update the UI
                var name = dataSnapshot.child(Constants.User.Name).value.toString()
                var id = dataSnapshot.child(Constants.User.Id).value.toString()
                var type = dataSnapshot.child(Constants.User.Type).value
                if (type!=null) {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.putExtra(Constants.User.Name,name)
                    intent.putExtra(Constants.User.Type,type.toString())
                    intent.putExtra(Constants.User.Id,id)
                    startActivity(intent)
                } else {
                    signInToFirebase()
                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w( "loadUser:onCancelled", databaseError.toException())
            }
        }

        val userDatabase = FirebaseDatabase.getInstance().reference
            .child(Constants.Database.Users).child(firebaseUser?.uid!!)

        userDatabase.addValueEventListener(userListener)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            if (response?.isNewUser!!) {
                val userDatabase = FirebaseDatabase.getInstance().reference
                    .child(Constants.Database.Users)
                val userObject : MutableMap<String?, Any> = mutableMapOf()
                var user = User(id = firebaseUser?.uid!!,
                    name = firebaseUser?.displayName!!,
                    userType = userType!!)

                userObject[user.id] = user
                userDatabase.updateChildren(userObject).addOnSuccessListener {
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                }.addOnFailureListener {
                    Snackbar.make(
                        view!!,
                        String.format(getString(R.string.error),it.message),
                        Snackbar.LENGTH_LONG).show()
                }
            } else {
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            val errorMessage = Snackbar.make(view!!,
                String.format(getString(R.string.error),response?.error?.message),
                Snackbar.LENGTH_INDEFINITE)
            errorMessage.setAction(getString(R.string.ok)) {
                    errorMessage.dismiss()
            }
            errorMessage.show()
        }
    }

    private fun signInToFirebase() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    override fun onResume() {
        super.onResume()
        if (firebaseUser!=null){
            checkIfUserIsInDatabase()
        } else {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }
}