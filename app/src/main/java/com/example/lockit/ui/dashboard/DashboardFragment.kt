package com.example.lockit.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lockit.Adapter.PasswordAdapter
import com.example.lockit.AddPasswordActivity
import com.example.lockit.PasswordGeneraterAcitvity
import com.example.lockit.R
import com.example.lockit.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_password_dialog.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.notes_item_layout.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    var recyclerView:RecyclerView? = null
    private var passwordAdapter:PasswordAdapter? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardViewModel.passwordModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            passwordAdapter = context?.let { it1-> PasswordAdapter(it1,it) }
            recyclerView!!.adapter = passwordAdapter
            passwordAdapter!!.notifyDataSetChanged()
        })
        checkPassword()


        root.addPassword.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.add_password_dialog, null)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.addPassword_ll.setOnClickListener {
                val intent = Intent(context,AddPasswordActivity::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            }

            dialogView.generatePassword_ll.setOnClickListener {
                val intent = Intent(context,PasswordGeneraterAcitvity::class.java)
                startActivity(intent)
            }
        }

        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.passwordRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun checkPassword(){
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.progress_dialog)
        progressDialog.show()

        val data = FirebaseDatabase.getInstance().reference.child("Passwords")

        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    emptyNotes_password.visibility = View.GONE
                    instructionTV_password.visibility = View.GONE
                    passwordRV.visibility = View.VISIBLE
                    progressDialog.dismiss()
                }else{
                    emptyNotes_password.visibility = View.VISIBLE
                    instructionTV_password.visibility = View.VISIBLE
                    passwordRV.visibility = View.GONE
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                emptyNotes_password.visibility = View.GONE
                instructionTV_password.visibility = View.GONE
                passwordRV.visibility = View.VISIBLE
                progressDialog.dismiss()
            }

        })
    }
}