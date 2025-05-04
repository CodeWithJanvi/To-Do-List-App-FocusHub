package com.example.myapplication1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.EditText


class EditTaskActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHandler
    private var currentTaskId: Int = -1 // ✅ Fixed the property name

    private lateinit var editTaskTitle: EditText
    private lateinit var editTaskDate: EditText
    private lateinit var editTaskTime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        db = DatabaseHandler(this)

        // ✅ Initialize Views
        editTaskTitle = findViewById(R.id.editTaskTitle)
        editTaskDate = findViewById(R.id.editTaskDate)
        editTaskTime = findViewById(R.id.editTaskTime)

        // ✅ Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Task"

        // ✅ Get task ID from intent
        currentTaskId = intent.getIntExtra("COLUMN_TASK_ID", -1)
        if (currentTaskId != -1) {
            loadTask(currentTaskId)
        }
    }

    // ✅ Load task data into EditTexts
    private fun loadTask(id: Int) {
        val task: Task? = db.getTaskById(id)
        task?.let {
            editTaskTitle.setText(it.taskTitle)
            editTaskDate.setText(it.taskDate)
            editTaskTime.setText(it.taskTime)
        }
    }

    // ✅ Handle back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
