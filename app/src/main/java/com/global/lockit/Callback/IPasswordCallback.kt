package com.global.lockit.Callback

import com.global.lockit.Model.PasswordModel

interface IPasswordCallback {
        fun onPasswordLoadFailed(str:String)
        fun onPasswordLoadSuccess(list:List<PasswordModel>)
}