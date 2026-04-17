package com.example.appcar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.ServiceListAdapter
import com.example.appcar.client.BookingClientActivity
import com.example.appcar.client.HistoryClientActivity
import com.example.appcar.client.ProfileClientActivity
import com.example.appcar.client.ServiceItem
import com.example.appcar.database.ServiceDao
import com.example.appcar.database.UserDAO
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val rvServices = findViewById<RecyclerView>(R.id.rvServices)
        rvServices.setHasFixedSize(true)
        rvServices.isNestedScrollingEnabled = false
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)

        val userDAO = UserDAO(this)

        // nhận email từ LoginActivity
        val email = intent.getStringExtra("EMAIL")

        if (email != null) {
            val userInfo = userDAO.getUserInfoByEmail(email)

            if (userInfo != null) {
                val fullName = userInfo.first
                val address = userInfo.second

                tvUserName.text = "Xin chào, $fullName"
                tvLocation.text = "Địa chỉ: $address"
            }
        }

        val serviceDao = ServiceDao(this)
        val services = serviceDao.getAllServicesList()
        val adapter = ServiceListAdapter(services) { item ->

            val intent = Intent(this, BookingClientActivity::class.java)

            // truyền dữ liệu sang màn đặt lịch
            intent.putExtra("service_name", item.name)
            intent.putExtra("service_price", item.priceText)

            startActivity(intent)
        }
        rvServices.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvServices.adapter = adapter
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> true

                R.id.nav_booking -> {
                    navigate(this, BookingClientActivity::class.java)
                    true
                }

                R.id.nav_history -> {
                    navigate(this, HistoryClientActivity::class.java)
                    true
                }

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