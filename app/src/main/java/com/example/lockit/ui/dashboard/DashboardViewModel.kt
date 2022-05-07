package com.example.lockit.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lockit.Callback.IPasswordCallback
import com.example.lockit.Model.PasswordModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class DashboardViewModel : ViewModel(), IPasswordCallback {

    private var passwordLiveData:MutableLiveData<List<PasswordModel>>? = null
    private val passwordCallback:IPasswordCallback = this
    private var messageError:MutableLiveData<String>? = null

    val passwordModel:MutableLiveData<List<PasswordModel>>?
    get() {
        if (passwordLiveData == null){
            passwordLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadPassword()
        }
        val mutableLiveData = passwordLiveData
        return mutableLiveData
    }

    private fun loadPassword() {
        val passwordList = ArrayList<PasswordModel>()
        val database = FirebaseDatabase.getInstance().reference.child("Passwords")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                passwordList.clear()
                for (snapshot in snapshot.children){
                    val passModel = snapshot.getValue(PasswordModel::class.java)
                    passwordList.add(passModel!!)
                }
                passwordCallback.onPasswordLoadSuccess(passwordList)
            }

            override fun onCancelled(error: DatabaseError) {
                passwordCallback.onPasswordLoadFailed(error.message)
            }

        })
    }

    override fun onPasswordLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onPasswordLoadSuccess(list: List<PasswordModel>) {
        val mutableLiveData = passwordLiveData
        mutableLiveData!!.value = list
    }
}