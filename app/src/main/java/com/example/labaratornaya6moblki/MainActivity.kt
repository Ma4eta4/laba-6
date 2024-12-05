package com.example.labaratornaya6moblki

import android.app.AlarmManager
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.labaratornaya6moblki.DataBase.ReminderDatabaseHelper
import com.example.labaratornaya6moblki.Notification.ReminderReceiver

import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSetDate: Button
    private lateinit var buttonSetTime: Button
    private lateinit var buttonSaveReminder: Button
    private lateinit var buttonViewReminders: Button

    private var reminderDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSetDate = findViewById(R.id.buttonSetDate)
        buttonSetTime = findViewById(R.id.buttonSetTime)
        buttonSaveReminder = findViewById(R.id.buttonSaveReminder)
        buttonViewReminders = findViewById(R.id.buttonViewReminders)

        buttonSetDate.setOnClickListener {
            showDatePicker()
        }

        buttonSetTime.setOnClickListener {
            showTimePicker()
        }

        buttonSaveReminder.setOnClickListener {
            saveReminder()
        }

        buttonViewReminders.setOnClickListener {
            val intent = Intent(this, ReminderListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            reminderDate.set(Calendar.YEAR, year)
            reminderDate.set(Calendar.MONTH, month)
            reminderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }, reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH), reminderDate.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            reminderDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
            reminderDate.set(Calendar.MINUTE, minute)
        }, reminderDate.get(Calendar.HOUR_OF_DAY), reminderDate.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    private fun saveReminder() {
        val title = editTextTitle.text.toString()
        val message = editTextMessage.text.toString()
        val date = reminderDate.timeInMillis.toString()

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        // Сохранение напоминания в базу данных
        val dbHelper = ReminderDatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ReminderDatabaseHelper.COLUMN_TITLE, title)
            put(ReminderDatabaseHelper.COLUMN_MESSAGE, message)
            put(ReminderDatabaseHelper.COLUMN_DATE, date)
        }

        val newRowId = db.insert(ReminderDatabaseHelper.TABLE_NAME, null, values)

        if (newRowId != -1L) {
            Toast.makeText(this, "Напоминание сохранено", Toast.LENGTH_SHORT).show()

            // Установка уведомления через AlarmManager
            setReminderAlarm(reminderDate.timeInMillis, title, message)
        } else {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setReminderAlarm(reminderTime: Long, title: String, message: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminderTime.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
    }
}