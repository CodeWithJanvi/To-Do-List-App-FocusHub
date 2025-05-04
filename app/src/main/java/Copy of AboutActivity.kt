package com.example.myapplication1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setTitle("About")

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val logoutButton = findViewById<Button>(R.id.btn_logout)
        val usernameTextView = findViewById<TextView>(R.id.user_name)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Retrieve username from SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Guest") // Default to "Guest" if not found
        usernameTextView.text = username

        // Show toast when rating changes
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(this, "Thanks for rating: $rating stars!", Toast.LENGTH_SHORT).show()
        }

        // Logout functionality
        logoutButton.setOnClickListener {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            sharedPref.edit().clear().apply()

            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Set Bottom Navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_task -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_star -> {
                    startActivity(Intent(this, StarActivity::class.java))
                    finish()
                    true
                }
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
                R.id.nav_about -> true // Stay on AboutActivity
                else -> false
            }
        }

        // Highlight the About tab in Bottom Navigation
        bottomNavigationView.selectedItemId = R.id.nav_about
    }
}
