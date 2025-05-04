package com.example.myapplication1

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalendarActivity : AppCompatActivity() {
    private lateinit var selectedDateTextView: TextView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: CalenderTaskAdapter
    private lateinit var dbHelper: DatabaseHandler
    private lateinit var bottomNavigationView: BottomNavigationView
    private var taskList: MutableList<Task> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        title = "Calendar View"

        selectedDateTextView = findViewById(R.id.selectedDate)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        taskRecyclerView = findViewById(R.id.recyclerViewTasks)
        bottomNavigationView = findViewById(R.id.bottom_navigation) // ✅ Find BottomNavigationView

        dbHelper = DatabaseHandler(this)

        taskAdapter = CalenderTaskAdapter(taskList)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        // ✅ Handle date selection from CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            selectedDateTextView.text = "Selected Date: $selectedDate"

            Log.d("DB_DEBUG", "Fetching tasks for date: $selectedDate") // Debug log
            fetchTasksForDate(selectedDate) // ✅ Fetch tasks based on selected date
        }

        // ✅ Set up Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_task -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_star -> {
                    startActivity(Intent(this, StarActivity::class.java))
                    true
                }
                R.id.nav_calendar -> true
                R.id.nav_activity -> {
                    startActivity(Intent(this, MyActivity::class.java))
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchTasksForDate(selectedDate: String) {
        taskList.clear() // Clear previous tasks
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM tasks WHERE date = ?"
            cursor = db.rawQuery(query, arrayOf(selectedDate))

            if (cursor.count == 0) {
                Log.d("DB_DEBUG", "No tasks found for date: $selectedDate")
            }

            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex("id")
                val titleIndex = cursor.getColumnIndex("task_title")
                val dateIndex = cursor.getColumnIndex("date")
                val timeIndex = cursor.getColumnIndex("time")
                val starredIndex = cursor.getColumnIndex("is_starred")
                val completedIndex = cursor.getColumnIndex("isCompleted")

                if (idIndex == -1 || titleIndex == -1 || dateIndex == -1 || timeIndex == -1 || starredIndex == -1 || completedIndex == -1) {
                    Log.e("DB_ERROR", "One or more column indexes are missing!")
                }

                val id = cursor.getInt(idIndex)
                val title = cursor.getString(titleIndex)
                val taskDate = cursor.getString(dateIndex)
                val taskTime = cursor.getString(timeIndex)
                val isStarred = cursor.getInt(starredIndex) == 1
                val isCompleted = if (completedIndex != -1) cursor.getInt(completedIndex) == 1 else false

                Log.d(
                    "DB_DEBUG",
                    "Task Loaded: ID=$id, Title=$title, Date=$taskDate, Time=$taskTime, Starred=$isStarred, Completed=$isCompleted"
                )

                taskList.add(Task(id, title, taskDate, taskTime, isCompleted, isStarred))
            }
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error fetching tasks: ${e.message}")
        } finally {
            cursor?.close()
            db.close()
        }

        taskAdapter.notifyDataSetChanged() // Update RecyclerView
    }
}
