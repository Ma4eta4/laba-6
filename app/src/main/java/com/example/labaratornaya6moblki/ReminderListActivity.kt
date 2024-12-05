package com.example.labaratornaya6moblki

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.labaratornaya6moblki.DataBase.ReminderDatabaseHelper

class ReminderListActivity : AppCompatActivity() {

    private lateinit var listViewReminders: ListView
    private lateinit var reminderAdapter: ArrayAdapter<String>
    private val reminders = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        listViewReminders = findViewById(R.id.listViewReminders)

        // Получаем данные из базы данных
        loadReminders()

        reminderAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reminders)
        listViewReminders.adapter = reminderAdapter

        // Можно добавить обработчик кликов для удаления или редактирования напоминаний
        listViewReminders.setOnItemClickListener { _, _, position, _ ->
            val reminder = reminders[position]
            deleteReminder(reminder)
            reminders.removeAt(position)
            reminderAdapter.notifyDataSetChanged()
        }
    }

    private fun loadReminders() {
        val dbHelper = ReminderDatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${ReminderDatabaseHelper.TABLE_NAME}", null)
        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabaseHelper.COLUMN_TITLE))
            reminders.add(title)
        }
        cursor.close()
    }

    private fun deleteReminder(title: String) {
        val dbHelper = ReminderDatabaseHelper(this)
        val db = dbHelper.writableDatabase
        db.delete(ReminderDatabaseHelper.TABLE_NAME, "${ReminderDatabaseHelper.COLUMN_TITLE} = ?", arrayOf(title))
    }
}
