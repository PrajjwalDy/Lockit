package com.example.lockit.Callback

import com.example.lockit.Model.PasswordModel

interface IPasswordCallback {
        fun onPasswordLoadFailed(str:String)
        fun onPasswordLoadSuccess(list:List<PasswordModel>)
}