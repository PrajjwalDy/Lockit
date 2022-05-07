package com.example.lockit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lockit.Model.PasswordGenerator
import kotlinx.android.synthetic.main.activity_password_generater_acitvity.*

class PasswordGeneraterAcitvity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_generater_acitvity)

        val passwordGenerator = PasswordGenerator()


        generatePassword_btn.setOnClickListener {
            val length = password_length.text.toString().toInt()
            val specialKeyword = specialKeyword.text.toString()
            val password:String = passwordGenerator.generatePassword(length,specialKeyword)
            generatedPassword_TV.text = password
        }
    }



}