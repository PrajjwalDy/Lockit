package com.example.lockit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lockit.Adapter.NotesAdapter
import com.example.lockit.AddNotesActivity
import com.example.lockit.R
import com.example.lockit.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    var recyclerView:RecyclerView? = null
    private var notesAdapter:NotesAdapter? = null

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
            notesAdapter = context?.let { it1-> NotesAdapter(it1,it) }
            recyclerView!!.adapter = notesAdapter
            notesAdapter!!.notifyDataSetChanged()
        })

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}