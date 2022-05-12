package com.global.lockit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.global.lockit.Model.UserModel
import com.global.lockit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_notes.*
import kotlinx.android.synthetic.main.activity_add_password.*

class AddPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_password)

        fetchDefaultPin()
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
            postMap["passTitle"] = passwordTitle_tv.text.toString().trim{ it <= ' '}
            postMap["creator"] = FirebaseAuth.getInstance().currentUser!!.uid
            postMap["pin"] = pin_ET.text.toString().trim{ it <= ' '}
            postMap["password"] = password_ET.text.toString().trim{ it <= ' '}

            ref.child(helpId).updateChildren(postMap)

            passwordTitle_tv.text.clear()
            password_ET.text.clear()
            pin_ET.text.clear()
            Toast.makeText(this,"Password Saved", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun fetchDefaultPin(){
        val data = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        data.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("defaultPin")){
                    val value = snapshot.getValue(UserModel::class.java)
                    pin_ET.setText(value!!.defaultPin)
                    Toast.makeText(this@AddPasswordActivity,"Auto fill default pin success",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@AddPasswordActivity,"You don't have any default pin",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddPasswordActivity,"No Internet Connection",Toast.LENGTH_SHORT).show()
            }

        })
    }
}
