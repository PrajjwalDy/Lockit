package com.example.lockit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_notes.*
import kotlinx.android.synthetic.main.activity_add_password.*

class AddPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_password)

        savePassword.setOnClickListener {
            addPassword()
        }
    }

    private fun addPassword(){
        if(passwordTitle_tv.text.isEmpty()|| password_ET.text.isEmpty()){
            Toast.makeText(this,"Enter the required credential", Toast.LENGTH_LONG).show()
        }else{
            val ref = FirebaseDatabase.getInstance().reference.child("Passwords")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            val helpId = ref.push().key

            val postMap = HashMap<String,Any>()
            postMap["passwordId"] = helpId!!
            postMap["passTitle"] = passwordTitle_tv.text.toString()
            postMap["creator"] = FirebaseAuth.getInstance().currentUser!!.uid
            postMap["pin"] = pin_ET.text.toString()
            postMap["password"] = password_ET.text.toString()

            ref.child(helpId).updateChildren(postMap)

            passwordTitle_tv.text.clear()
            password_ET.text.clear()
            pin_ET.text.clear()
            Toast.makeText(this,"Password Saved", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
