package com.example.myapplication1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHandler
    private lateinit var btnPending: Button
    private lateinit var btnCompleted: Button
    private lateinit var txtTaskCount: TextView
    private lateinit var taskProgressBar: ProgressBar
    private lateinit var txtMotivation: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        title = "Task Manager"

        try {
            db = DatabaseHandler(this)
        } catch (e: Exception) {
            showToast("Database Error: ${e.message}")
            Log.e("MyActivity", "Database Initialization Error", e)
        }

        // Initialize Views
        btnPending = findViewById(R.id.btnPending)
        btnCompleted = findViewById(R.id.btnCompleted)
        txtTaskCount = findViewById(R.id.txtTaskCount)
        taskProgressBar = findViewById(R.id.taskProgressBar)
        txtMotivation = findViewById(R.id.txtMotivation)
        bottomNavigationView = findViewById(R.id.bottom_navigation) // ✅ Added navigation view

        // Load Task Statistics
        updateTaskStats()

        // Button Click Listeners
        btnPending.setOnClickListener { openTaskListActivity("pending") }
        btnCompleted.setOnClickListener { openTaskListActivity("completed") }

        // ✅ Set Bottom Navigation Listener
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
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }
                R.id.nav_activity -> true
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTaskStats() // Refresh UI when returning
    }

    @SuppressLint("SetTextI18n")
    private fun updateTaskStats() {
        try {
            val pendingTasks = db.getPendingTasks().orEmpty()
            val completedTasks = db.getCompletedTasks().orEmpty()

            val totalTasks = pendingTasks.size + completedTasks.size
            val completedCount = completedTasks.size
            val pendingCount = pendingTasks.size

            // Update UI
            txtTaskCount.text = "Tasks: $completedCount Completed | $pendingCount Pending | $totalTasks Total"
            taskProgressBar.progress = if (totalTasks > 0) (completedCount * 100 / totalTasks) else 0
            txtMotivation.text = getMotivationalQuote(completedCount, totalTasks)

        } catch (e: Exception) {
            Log.e("MyActivity", "Error updating task stats", e)
            showToast("Error loading tasks")
        }
    }

    private fun getMotivationalQuote(completed: Int, total: Int): String {
        return when {
            total == 0 -> "Start your journey with a new task! 🚀"
            completed == total -> "Awesome! All tasks are completed! 🎉"
            completed > total / 2 -> "You're over halfway there! Keep pushing! 💪"
            completed > 0 -> "Good job! Every step counts! ✅"
            else -> "Let's get started on your tasks! 🏆"
        }
    }

    private fun openTaskListActivity(taskType: String) {
        try {
            val tasks = if (taskType == "pending") {
                db.getPendingTasks().map { it.taskTitle }
            } else {
                db.getCompletedTasks().map { it.taskTitle }
            }

            if (tasks.isEmpty()) {
                showToast("No $taskType tasks available.")
                return
            }

            val intent = Intent(this, TaskListActivity::class.java).apply {
                putExtra("taskType", taskType)
                putStringArrayListExtra("tasks", ArrayList(tasks))
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("MyActivity", "Error opening TaskListActivity", e)
            showToast("Error loading tasks")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}