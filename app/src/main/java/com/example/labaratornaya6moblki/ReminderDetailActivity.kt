package com.example.labaratornaya6moblki

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReminderDetailActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)

        textViewTitle.text = title
        textViewMessage.text = message
    }
}
