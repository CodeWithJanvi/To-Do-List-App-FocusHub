package com.example.myapplication1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH: Long = 3000 // Duration of the splash screen in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Handler to delay the transition to the main activity
        Handler().postDelayed({
            // Start the main activity
            val mainIntent = Intent(this, WelcomeActivity::class.java)
            startActivity(mainIntent)
            finish() // Close the splash activity
        }, SPLASH_DISPLAY_LENGTH)
    }
}

