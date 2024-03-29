package com.global.lockit.Adapter

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.global.lockit.Model.NotesModel
import com.global.lockit.NotesViewActivity
import com.global.lockit.R

class NotesAdapter(private val mContext:Context,
                   private val mNotes: List<NotesModel>):RecyclerView.Adapter<com.global.lockit.Adapter.NotesAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView){

                           val title:TextView = itemView.findViewById(R.id.notesTitle_IV) as TextView
                           val notes:TextView = itemView.findViewById(R.id.notesIV) as TextView

                           fun bind(list:NotesModel){
                               title.text = list.title
                               notes.text = list.note
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notes_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesAdapter.ViewHolder, position: Int) {
        holder.bind(mNotes[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, NotesViewActivity::class.java)
            intent.putExtra("notesId",mNotes[position].noteId)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mNotes.size
    }

}