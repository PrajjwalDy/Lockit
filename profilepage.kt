package com.hindu.cunow.Activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.FacultyData
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_landing_page.*

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        landing_faculty_button.setOnClickListener {
            val intent = Intent(this, FacultyLogin::class.java)
            startActivity(intent)
            finish()
        }

        landing_student_button.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(FirebaseAuth.getInstance().currentUser != null){


            checkUser(user)

        }
    }

    private fun checkStatus(){
        val data = FirebaseDatabase.getInstance()
            .reference
            .child("Faculty")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val faculty = snapshot.getValue(FacultyData::class.java)
                if (faculty!!.F_Verified){
                    val intent = Intent(this@LandingPageActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }else{
                    Toast.makeText(this@LandingPageActivity, "Account isn't verified yet!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LandingPageActivity, FacultyVerificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LandingPageActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun checkUser(user : FirebaseUser?){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        mainLL.visibility = View.GONE

        val userData = FirebaseDatabase.getInstance()
            .reference.child("Faculty")

        userData.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(FacultyData::class.java)
                if(data!!.Faculty_Y){
                    checkStatus()
                }else{
                    if (user!!.isEmailVerified){
                        val intent = Intent(this@LandingPageActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
                progressDialog.dismiss()
                mainLL.visibility = View.VISIBLE

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }



        })

    }
}
