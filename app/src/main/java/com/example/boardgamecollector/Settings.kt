package com.example.boardgamecollector

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File

class Settings : AppCompatActivity() {

    private val mapper = jacksonObjectMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setButtonsListeners()
    }

    private fun setButtonsListeners() {

        saveUsername.setOnClickListener{
            val userPath = this.filesDir.toString().plus("/user.json")
            val userSettingsFile = File(userPath)
            if(!userSettingsFile.exists()) userSettingsFile.createNewFile()
            val user = UserSettings(usernameField.text.toString(), 0, 0, "Synchronize to get data!")
            mapper.writeValue(File(userPath), user)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}