package com.global.lockit

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_notes.*

class AddNotesActivity : AppCompatActivity() {

    private  var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        storagePostImageRef = FirebaseStorage.getInstance().reference.child("NotesMedia")

        saveNote_btn.setOnClickListener {
            addNotes()
        }

        attachMedia_btn.setOnClickListener {
            attachMedia()
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

    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Please wait")
        progressDialog.show()

        val fileRef = storagePostImageRef!!.child(System.currentTimeMillis().toString()+".jpg")

        val uploadTask:StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                    progressDialog.dismiss()
                }

            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{task->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Notes")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                val notesId = ref.push().key

                val postMap = HashMap<String,Any>()
                postMap["noteId"] = notesId!!
                postMap["title"] = notesTitel_tv.text.toString()
                postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                postMap["note"] = note_tv.text.toString()
                postMap["media"] = myUrl

                ref.child(notesId).updateChildren(postMap)
                Toast.makeText(this,"Image shared successfully",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                startActivity(Intent(this@AddNotesActivity, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }

    private fun attachMedia(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data
            mediaImageView.setImageURI(imageUri)
            uploadImage()
        }

    }
}