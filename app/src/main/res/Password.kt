package com.hindu.cunow.Fragments.Academics

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.FacultyData
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentAcademicsBinding
import com.hindu.cunow.databinding.FragmentHomeBinding
import com.hindu.cunow.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_academics.*

class AcademicsFragment : Fragment() {

    private lateinit var viewModel: AcademicsViewModel
    private var _binding: FragmentAcademicsBinding? = null
    private  val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AcademicsViewModel::class.java)

        _binding = FragmentAcademicsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        checkUser(user)
        return root
        //return inflater.inflate(R.layout.fragment_academics, container, false)
    }


    private fun checkUser(user : FirebaseUser?){
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()

        val userData = FirebaseDatabase.getInstance()
            .reference.child("Faculty")

        userData.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(FacultyData::class.java)
                if(data!!.Faculty_Y){
                    addSubjects.visibility = View.VISIBLE
                }else{
                    addSubjects.visibility = View.GONE
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }



        })

    }

}
