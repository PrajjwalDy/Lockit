package com.example.lockit.Model

import java.lang.StringBuilder

class PasswordGenerator {
    private val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#=%()&><:;{}+_$~|/"

    fun generatePassword(length: Int, specialWord: String = ""): String {
        val sb = StringBuilder(length)

        for (x in 0 until length){
            val random = (characters.indices.random())
            sb.append(characters[random])
        }
        sb.insert((0 until length).random(),specialWord)

        return sb.toString()
    }
}