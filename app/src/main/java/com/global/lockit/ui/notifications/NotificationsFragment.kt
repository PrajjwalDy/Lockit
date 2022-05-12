package com.global.lockit.ui.notifications

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.global.lockit.LogInActivity
import com.global.lockit.Model.UserModel
import com.global.lockit.PrivacyPolicyActivity
import com.global.lockit.R
import com.global.lockit.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.default_pin_dialog.view.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.menu_dialog.view.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadUserDetails()


        root.menu.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.menu_dialog, null)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.default_ll.setOnClickListener {

                val pinDialogView = LayoutInflater.from(context).inflate(R.layout.default_pin_dialog,null)
                val pinDialogBuilder = AlertDialog.Builder(context)
                    .setView(pinDialogView)
                val pinAlertDialog = pinDialogBuilder.show()

                pinDialogView.set_pin_btn.setOnClickListener {
                    val pin = pinDialogView.defaultPinEditText.text.toString().trim{ it <= ' '}
                    val password = pinDialogView.password_et_dp.text.toString().trim{ it <= ' '}
                    if (password == userPassword.text.toString()){
                        setDefaultPin(pin)
                    }else{
                        Toast.makeText(context,"Wrong password",Toast.LENGTH_SHORT).show()
                    }

                    pinAlertDialog.dismiss()
                }

                alertDialog.dismiss()
            }

            dialogView.privacyPolicy_ll.setOnClickListener {
                val intent = Intent(context, PrivacyPolicyActivity::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            }

            dialogView.logout_ll.setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(context, LogInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }

        return root
    }

    private fun loadUserDetails(){
        val data = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    userFullName.text = user!!.name
                    userEmail.text = user.email
                    userPassword.text = user.password
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userFullName.text = "Check your connection"
                userEmail.text = "Check your connection"
            }

        })
    }

    private fun setDefaultPin(pin:String){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val dataRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["defaultPin"] = pin

        dataRef.child(uid).updateChildren(dataMap)

        Toast.makeText(context,"Default pin set success",Toast.LENGTH_SHORT).show()
    }

}