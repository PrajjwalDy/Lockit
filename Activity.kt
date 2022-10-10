package com.hindu.cunow.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.hindu.cunow.Adapter.ChatAdapter
import com.hindu.cunow.Model.ChatModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_circle_flow.*
import kotlinx.android.synthetic.main.activity_comment.*

class ChatActivity : AppCompatActivity() {
    private var profileId = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageChatImageRef: StorageReference? =null
    private var media = ""

    private var chatList:MutableList<ChatModel>? = null
    private var chatAdapter:ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        storageChatImageRef = FirebaseStorage.getInstance().reference.child("Chat Flow")

        val intent = intent
        profileId = intent.getStringExtra("uid").toString()

        val recyclerView:RecyclerView = findViewById(R.id.chatRV)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        chatList = ArrayList()
        chatAdapter = ChatAdapter(this, chatList as ArrayList<ChatModel>)
        recyclerView.adapter = chatAdapter

        selectMedia_chat.setOnClickListener {
            cropImage()
            media = "yes"
        }

        send_chat.setOnClickListener { view->
            if (imageUri != null){
                uploadImage()
            }else{
                sendMessage(view)
            }
        }

        loadUserData()
        retrieveChat()

    }

    private fun cropImage(){
        CropImage.activity()
            .start(this)
    }

    private fun sendMessage(view:View){
        if (chat_message.text.isEmpty()) {
            Snackbar.make(view, "please write something..", Snackbar.LENGTH_SHORT).show()
        } else {
            FirebaseDatabase.getInstance().reference.child("ChatList")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(profileId).setValue(true)

            FirebaseDatabase.getInstance().reference.child("ChatList")
                .child(profileId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(true)

            val dataRef = FirebaseDatabase.getInstance().reference
                .child("ChatData")

            val commentId = dataRef.push().key

            val dataMap = HashMap<String, Any>()
            dataMap["messageId"] = commentId!!
            dataMap["chatText"] = chat_message.text.toString()
            dataMap["sender"] = FirebaseAuth.getInstance().currentUser!!.uid
            dataMap["receiver"] = profileId
            dataMap["containImage"] = false

            dataRef.push().setValue(dataMap)
            chat_message.text.clear()
            FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
                .child(profileId)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(commentId).setValue(true)
        }
    }

    private fun uploadImage(){
        val fileReference = storageChatImageRef!!
            .child(System.currentTimeMillis().toString()+".jpg")

        FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(profileId)
            .setValue(true)

        FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(profileId)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(true)

        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("ChatData")

        val chatId = ref.push().key

        val dataMap = HashMap<String, Any>()
        dataMap["messageId"] = chatId!!
        dataMap["chatText"] = chat_message.text.toString()
        dataMap["sender"] = FirebaseAuth.getInstance().currentUser!!.uid
        dataMap["containImage"] = true
        dataMap["receiver"] = profileId
        dataMap["chatImage"] = ""

        ref.child(chatId).updateChildren(dataMap)

        val uploadTask:StorageTask<*>
        uploadTask = fileReference.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileReference.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()


                val dataMap = HashMap<String, Any>()
                dataMap["messageId"] = chatId
                dataMap["sender"] = FirebaseAuth.getInstance().currentUser!!.uid
                dataMap["receiver"] = profileId
                dataMap["containImage"] = true
                dataMap["chatImage"] = myUrl

                ref.child(chatId).updateChildren(dataMap)
                Toast.makeText(this,"Image Sent", Toast.LENGTH_SHORT).show()

                FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
                    .child(profileId)
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(chatId).setValue(true)
                media = ""
                imageUri = null

            }else{
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveChat(){
        val chatData = FirebaseDatabase.getInstance().reference.child("ChatData")

        chatData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    chatList!!.clear()
                    for (snapshot in snapshot.children){
                        val chat = snapshot.getValue(ChatModel::class.java)

                        if (chat!!.receiver.equals(FirebaseAuth.getInstance().currentUser!!.uid) && chat.sender.equals(profileId)
                            ||chat.receiver.equals(profileId) && chat.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid) ){
                            chatList!!.add(chat!!)
                        }

                    }
                    chatAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadUserData(){
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(profileId)

        usersRef.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        Glide.with(this@ChatActivity).load(user!!.profileImage).into(chatUser_Image)
                        userName_chat.text = user.fullName
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
        }
    }

}
