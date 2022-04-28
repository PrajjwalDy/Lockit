package com.example.lockit

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUp_button.setOnClickListener {
            signUp()
        }

        toLogIn_activity.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signUp(){
        val fullName = fullName_signUp.text.toString()
        val phone = phone_signup.text.toString()
        val email = email_SignUp.text.toString()
        val password = password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Please enter the Full Name",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Please enter the email",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(phone) -> Toast.makeText(this,"Please enter the phone",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Please enter the password",Toast.LENGTH_SHORT).show()

            else->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("SingingUp")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth:FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            saveUserInfo(fullName,email,password,progressDialog)
                        }else{
                            val message = task.exception.toString()
                            Toast.makeText(this, "Some Error Occurred: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(name:String,email:String, password:String,progressDialog:ProgressDialog) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["uid"] = uid
        dataMap["name"] = name
        dataMap["password"] = password
        dataMap["email"] = email
        dataMap["profileImage"] ="https://www.focusedu.org/wp-content/uploads/2018/12/circled-user-male-skin-type-1-2.png"

        usersRef.child(uid).setValue(dataMap)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"SignUp Success",Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignUpActivity,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val message = task.exception.toString()
                    Toast.makeText(this,"Some error occurred while creating your account:$message",Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}