package com.example.appcar.client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.HomeActivity
import com.example.appcar.R
import com.example.appcar.adapter.BookingAdapter
import com.example.appcar.database.Booking
import com.example.appcar.database.BookingDAO
import com.example.appcar.database.UserDAO
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryClientActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var bookingDAO: BookingDAO
    private lateinit var userDAO: UserDAO
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()
    private var currentUserId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_client)

        // Khởi tạo DAO và View mới thêm
        bookingDAO = BookingDAO(this)
        userDAO = UserDAO(this)
        rvHistory = findViewById(R.id.rvHistory)

        // Lấy User ID từ SharedPreferences
        val sharedPref = getSharedPreferences("AppPref", MODE_PRIVATE)
        val userEmail = sharedPref.getString("user_email", null)
        if (userEmail != null) {
            currentUserId = userDAO.getUserIdByEmail(userEmail) ?: -1
        }

        // Thiết lập RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = BookingAdapter(bookingList) { booking ->
            cancelBooking(booking)
        }
        rvHistory.adapter = adapter

        // Load dữ liệu
        loadHistory()

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

    private fun loadHistory() {
        if (currentUserId != -1) {
            bookingList.clear()
            bookingList.addAll(bookingDAO.getBookingsByUser(currentUserId))
            adapter.notifyDataSetChanged()
        }
    }

    private fun cancelBooking(booking: Booking) {
        if (booking.status == "PENDING") {
            if (bookingDAO.deleteBooking(booking.id)) {
                Toast.makeText(this, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show()
                loadHistory()
            } else {
                Toast.makeText(this, "Hủy thất bại", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Chỉ có thể hủy lịch đang chờ (PENDING)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun navigate(activity: Activity, target: Class<*>) {
        val intent = Intent(activity, target)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
