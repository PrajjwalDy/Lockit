package com.example.lockit.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.lockit.Model.NotesModel
import com.example.lockit.Model.PasswordModel
import com.example.lockit.R
import kotlinx.android.synthetic.main.display_password_dialog.view.*
import kotlinx.android.synthetic.main.pass_pin_dialog.view.*

class PasswordAdapter(private val mContext: Context,
                      private val mNotes: List<PasswordModel>):RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

                          inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                              val title: TextView = itemView.findViewById(R.id.passwordTitle_IV) as TextView

                              fun bind(list:PasswordModel){
                                  title.text = list.passTitle
                              }
                          }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.password_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mNotes[position])

        holder.itemView.setOnClickListener {
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.pass_pin_dialog, null)

            val dialogBuilder = AlertDialog.Builder(mContext)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            val pDialogView = LayoutInflater.from(mContext).inflate(R.layout.display_password_dialog,null)
            val pDialogBuilder = AlertDialog.Builder(mContext).setView(pDialogView)


            pDialogView.title_password_dialogBox.text = mNotes[position].passTitle
            pDialogView.password_dialogbox.text = mNotes[position].password

            dialogView.authenticate_pin.setOnClickListener {
                if (dialogView.pinEditText.text.toString() == mNotes[position].pin!!){
                    alertDialog.dismiss()
                    pDialogBuilder.show()

                }else{
                    Toast.makeText(mContext,"wrong pin",Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotes.size
    }
}