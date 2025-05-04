package com.example.myapplication1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.widget.AppCompatButton
import java.util.*

class TaskActivity : AppCompatActivity() {

    private lateinit var taskTitleEditText: TextInputEditText
    private lateinit var setDateEditText: TextInputEditText
    private lateinit var setTimeEditText: TextInputEditText
    private lateinit var saveTaskButton: AppCompatButton
    private lateinit var backButton: ImageView
    private lateinit var calendar: Calendar
    private lateinit var db: DatabaseHandler  // Ensuring it's not nullable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // Initialize Database
        db = DatabaseHandler(this)

        // Initialize Views
        taskTitleEditText = findViewById(R.id.taskTitleEditText)
        setDateEditText = findViewById(R.id.setDateEditText)
        setTimeEditText = findViewById(R.id.setTimeEditText)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        backButton = findViewById(R.id.ivBack)

        calendar = Calendar.getInstance()

        // Click Listeners
        setDateEditText.setOnClickListener { showDatePickerDialog() }
        setTimeEditText.setOnClickListener { showTimePickerDialog() }
        saveTaskButton.setOnClickListener { saveTask() }
        backButton.setOnClickListener { finish() } // Closes activity
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            setDateEditText.setText(
                String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            )
        }, year, month, day).show()
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            setTimeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true).show()
    }

    private fun saveTask() {
        val title = taskTitleEditText.text.toString().trim()
        val date = setDateEditText.text.toString().trim()
        val time = setTimeEditText.text.toString().trim()

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            id = 0, // Auto-increment
            taskTitle = title,
            taskDate = date,
            taskTime = time,
            isCompleted = false, // Default value
            isStarred = false // Default value
        )

        // Save to database
        val insertResult = db.addTask(task)

        if (insertResult > 0) {
            Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show()
            finish() // Close activity
        } else {
            Toast.makeText(this, "Error saving task.", Toast.LENGTH_SHORT).show()
        }
    }
}
