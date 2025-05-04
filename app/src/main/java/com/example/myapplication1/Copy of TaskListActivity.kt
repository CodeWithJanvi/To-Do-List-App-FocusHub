package com.example.myapplication1

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TaskListActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        title = "Task Data"

        val textView = findViewById<TextView>(R.id.txtTitle)
        val listView = findViewById<ListView>(R.id.listView)
        val db = DatabaseHandler(this)

        // Get task type from intent (default to "pending" if null)
        val taskType = intent.getStringExtra("taskType") ?: "pending"
        val isPending = taskType == "pending"

        // Fetch tasks safely
        val tasks = try {
            if (isPending) db.getPendingTasks().orEmpty().map { it.taskTitle }
            else db.getCompletedTasks().orEmpty().map { it.taskTitle }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show()
            emptyList() // Return an empty list if an error occurs
        }

        // Update UI
        textView.text = "${if (isPending) "Pending" else "Completed"} Tasks (${tasks.size})"

        // Set up ListView adapter
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
    }
}
