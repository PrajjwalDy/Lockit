package com.example.lockit

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_notes.*

class AddNotesActivity : AppCompatActivity() {

    private  var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        saveNote_btn.setOnClickListener {
            addNotes()
        }

    }


    private fun addNotes(){
        if(notesTitel_tv.text.isEmpty()|| note_tv.text.isEmpty()){
            Toast.makeText(this,"Enter the required credential", Toast.LENGTH_LONG).show()
        }else{
            val ref = FirebaseDatabase.getInstance().reference.child("Notes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            val helpId = ref.push().key

            val postMap = HashMap<String,Any>()
            postMap["noteId"] = helpId!!
            postMap["title"] = notesTitel_tv.text.toString()
            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
            postMap["note"] = note_tv.text.toString()

            ref.child(helpId).updateChildren(postMap)

            note_tv.text.clear()
            notesTitel_tv.text.clear()
            Toast.makeText(this,"Note Added", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    private fun attachMedia(){

    }
}