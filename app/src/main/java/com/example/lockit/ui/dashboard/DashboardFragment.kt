package com.example.lockit.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lockit.AddPasswordActivity
import com.example.lockit.R
import com.example.lockit.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.add_password_dialog.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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


        root.addPassword.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.add_password_dialog, null)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.addPassword_ll.setOnClickListener {
                val intent = Intent(context,AddPasswordActivity::class.java)
                startActivity(intent)
            }

            dialogView.generatePassword_ll.setOnClickListener {

            }

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}