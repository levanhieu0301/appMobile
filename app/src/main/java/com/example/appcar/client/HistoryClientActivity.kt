package com.example.appcar.client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.HomeActivity
import com.example.appcar.R
import com.example.appcar.client.ProfileClientActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryClientActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_client)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Set tab hiện tại
        bottomNav.selectedItemId = R.id.nav_history

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    navigate(this, HomeActivity::class.java)
                    true
                }

                R.id.nav_booking -> {
                    navigate(this, BookingClientActivity::class.java)
                    true
                }

                R.id.nav_history -> true

                R.id.nav_account -> {
                    navigate(this, ProfileClientActivity::class.java)
                    true
                }

                else -> false
            }
        }
    }

    private fun navigate(activity: Activity, target: Class<*>) {
        val intent = Intent(activity, target)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}