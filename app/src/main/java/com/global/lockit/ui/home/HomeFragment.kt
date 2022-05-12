package com.global.lockit.ui.home

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
import com.global.lockit.Adapter.NotesAdapter
import com.global.lockit.AddNotesActivity
import com.global.lockit.R
import com.global.lockit.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    var recyclerView:RecyclerView? = null
    private var notesAdapter: NotesAdapter? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel.notesViewModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            notesAdapter = context?.let { it1->
                NotesAdapter(
                    it1,
                    it
                )
            }
            recyclerView!!.adapter = notesAdapter
            notesAdapter!!.notifyDataSetChanged()
        })
        checkNotes()

        root.addNotes.setOnClickListener {
            val intent = Intent(context, AddNotesActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.notesRV) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun checkNotes(){

        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.progress_dialog)
        progressDialog.show()

        val data = FirebaseDatabase.getInstance().reference.child("Notes")

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    emptyNotes.visibility = View.GONE
                    instructionTV.visibility = View.GONE
                    notesRV.visibility = View.VISIBLE
                    progressDialog.dismiss()
                }else{
                    emptyNotes.visibility = View.VISIBLE
                    instructionTV.visibility = View.VISIBLE
                    notesRV.visibility = View.GONE
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Internet may not available", Toast.LENGTH_LONG).show()
                emptyNotes.visibility = View.GONE
                instructionTV.visibility = View.GONE
                instructionTV.text = "No Internet"
                notesRV.visibility = View.VISIBLE
                progressDialog.dismiss()
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}