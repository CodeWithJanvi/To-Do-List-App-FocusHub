package com.example.myapplication1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var todoRecycler: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var db: DatabaseHandler
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Tasks"

        // Initialize database handler
        db = DatabaseHandler(this)

        // Find views
        todoRecycler = findViewById(R.id.todo_recycler)
        addButton = findViewById(R.id.add_button)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Setup RecyclerView
        setupRecyclerView()
        loadTasks()

        // Add Task Button Click
        addButton.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
        }

        // Bottom Navigation Item Click
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_task -> true
                R.id.nav_star -> {
                    startActivity(Intent(this, StarActivity::class.java))
                    true
                }
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }
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

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            context = this,
            taskList = taskList,
            db = db,
            onStarClick = { task -> toggleTaskStar(task) },
            onEditClick = { task, position -> showEditTaskDialog(task, position) },
            onDeleteClick = { task, position -> deleteTask(task, position) },
            onTaskChecked = { task, isChecked ->
                db.updateTaskCompletionStatus(task.id, isChecked)
                if (isChecked) {
                    db.moveTaskToCompleted(task) // ✅ Move to completed table
                } else {
                    db.moveTaskToPending(task) // ✅ Move back to pending table
                }
                loadTasks() // Refresh list after moving task
            }
        )

        todoRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun loadTasks() {
        taskList.clear() // Clear old tasks before loading new ones
        taskList.addAll(db.getPendingTasks()) // Fetch pending tasks
        taskAdapter.notifyDataSetChanged() // Refresh RecyclerView
    }

    private fun toggleTaskStar(task: Task) {
        db.updateTaskStarStatus(task.id, !task.isStarred)
        loadTasks()
        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
    }

    private fun showEditTaskDialog(task: Task, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.activity_edit_task, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editTaskTitle)
        val editDate = dialogView.findViewById<EditText>(R.id.editTaskDate)
        val editTime = dialogView.findViewById<EditText>(R.id.editTaskTime)

        editTitle.setText(task.taskTitle)
        editDate.setText(task.taskDate)
        editTime.setText(task.taskTime)

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                task.taskTitle = editTitle.text.toString().trim()
                task.taskDate = editDate.text.toString().trim()
                task.taskTime = editTime.text.toString().trim()

                db.updateTask(task)
                taskAdapter.notifyItemChanged(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTask(task: Task, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                db.deleteTask(task.id)
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                taskAdapter.notifyItemRangeChanged(position, taskList.size)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadTasks() // Refresh tasks when returning to activity
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
