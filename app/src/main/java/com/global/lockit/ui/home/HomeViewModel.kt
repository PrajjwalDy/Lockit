package com.global.lockit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.global.lockit.Callback.INotesCallback
import com.global.lockit.Model.NotesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel(), INotesCallback {
    private var notesLiveData:MutableLiveData<List<NotesModel>>? = null

    private val notesCallback: INotesCallback = this
    private var messageError:MutableLiveData<String>? = null

    val notesViewModel:MutableLiveData<List<NotesModel>>?
    get() {
        if (notesLiveData  == null){
            notesLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadNotes()
        }
        val mutableLiveData = notesLiveData
        return mutableLiveData
    }

    private fun loadNotes() {
        val notesList = ArrayList<NotesModel>()
        val database = FirebaseDatabase.getInstance().reference
            .child("Notes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (snapshot in snapshot.children){
                    val notesModel = snapshot.getValue(NotesModel::class.java)
                    notesList.add(notesModel!!)
                }
                notesCallback.onNotesLoadSuccess(notesList)
            }

            override fun onCancelled(error: DatabaseError) {
                notesCallback.onNotesLoadFailed(error.message)
            }

        })
    }

    override fun onNotesLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onNotesLoadSuccess(list: List<NotesModel>) {
        val mutableLiveData = notesLiveData
        mutableLiveData!!.value = list
    }
}