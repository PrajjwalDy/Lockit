package com.example.lockit.Callback

import com.example.lockit.Model.NotesModel

interface INotesCallback {
        fun onNotesLoadFailed(str:String)
        fun onNotesLoadSuccess(list:List<NotesModel>)

}