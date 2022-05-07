package com.example.lockit.ui.notifications

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.lockit.LogInActivity
import com.example.lockit.Model.UserModel
import com.example.lockit.R
import com.example.lockit.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                alertDialog.dismiss()
            }

            dialogView.privacyPolicy_ll.setOnClickListener {
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

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    userFullName.text = user!!.name
                    userEmail.text = user.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userFullName.text = "Check your connection"
                userEmail.text = "Check your connection"
            }

        })
    }

}