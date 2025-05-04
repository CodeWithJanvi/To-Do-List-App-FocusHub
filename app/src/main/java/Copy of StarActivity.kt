package com.example.myapplication1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class StarActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dbHelper: DatabaseHandler
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star)

        title = "Starred Tasks"

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.starRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = DatabaseHandler(this)
        loadStarredTasks()

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_star // Set selected item

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_task -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_star -> true
                R.id.nav_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_activity -> {
                    startActivity(Intent(this, MyActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadStarredTasks() {
        val starredTasks = dbHelper.getStarredTasks().toMutableList()
        taskAdapter = TaskAdapter(
            context = this,
            taskList = starredTasks,
            db = dbHelper,
            onStarClick = { task -> toggleTaskStar(task) },
            onEditClick = { _, _ -> },
            onDeleteClick = { _, _ -> },
            onTaskChecked = { _, _ -> }
        )
        recyclerView.adapter = taskAdapter
    }

    private fun toggleTaskStar(task: Task) {
        task.isStarred = !task.isStarred
        dbHelper.updateTask(task)
        loadStarredTasks()
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
