package com.example.lockit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.lockit.Model.NotesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_notes_view.*

class NotesViewActivity : AppCompatActivity() {

    private var notesId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_view)

        val intent = intent
        notesId = intent.getStringExtra("notesId").toString()

        loadNotes()

        deleteNote.setOnClickListener {
                deleteNote()

        }

    }

    private fun loadNotes() {
        val notesData = FirebaseDatabase.getInstance().reference.child("Notes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(notesId)
        notesData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(NotesModel::class.java)
                    notesTitle_NA.text = data!!.title
                    notes_NA.text = data.note
                    if (data.media == null) {
                        imageMedia.visibility = View.GONE
                    } else {
                        imageMedia.visibility = View.VISIBLE
                        Glide.with(this@NotesViewActivity).load(data.media).into(imageMedia)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun deleteNote(){
        FirebaseDatabase.getInstance().reference.child("Notes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(notesId)
            .removeValue()
        finish()
        startActivity(Intent(this,MainActivity::class.java))
        Toast.makeText(this, "Notes Deleted successfully",Toast.LENGTH_SHORT).show()

    }
}