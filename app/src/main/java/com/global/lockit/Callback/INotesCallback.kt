package com.global.lockit.Callback

import com.global.lockit.Model.NotesModel

interface INotesCallback {
        fun onNotesLoadFailed(str:String)
        fun onNotesLoadSuccess(list:List<NotesModel>)

}