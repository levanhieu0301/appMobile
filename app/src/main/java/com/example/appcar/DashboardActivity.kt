package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.RepairHistoryDAO
import com.example.appcar.database.UserDAO
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 1. Ánh xạ View
        val txtAdminInfo = findViewById<TextView>(R.id.txtAdminInfo)
        val tvStatUser = findViewById<TextView>(R.id.tvStatUser)
        val tvStatRevenue = findViewById<TextView>(R.id.tvStatRevenue)
        val btnLogoutAdmin = findViewById<ImageButton>(R.id.btnLogoutAdmin)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Khởi tạo DAO
        val userDAO = UserDAO(this)
        val repairDAO = RepairHistoryDAO(this)

        // Quay lại login
        btnBack.setOnClickListener { finish() }

        // 2. Hiển thị thông tin Admin
        val email = intent.getStringExtra("EMAIL")
        if (email != null) {
            val fullName = userDAO.getFullNameByEmail(email)
            txtAdminInfo.text = "Chào mừng trở lại, ${fullName ?: "Admin"}"
        }

        // 3. Cập nhật thông số thống kê
        val newUserCount = userDAO.getNewUsersToday()
        tvStatUser.text = "+$newUserCount"

        // Tính tổng tiền sửa xe
        val dailyRevenue = repairDAO.getTodayRevenue()
        tvStatRevenue.text = String.format(Locale.getDefault(), "%,.0f VNĐ", dailyRevenue)

        // 4. Thiết lập sự kiện cho các Card Menu chính
        setupCardMenu()

        // 6. Xử lý Đăng xuất
        btnLogoutAdmin.setOnClickListener {
            val prefs = getSharedPreferences("USER", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupCardMenu() {
        findViewById<MaterialCardView>(R.id.btnManagePromo).setOnClickListener {
            startActivity(Intent(this, ManagePromotionActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnManageRepairHistory).setOnClickListener {
            startActivity(Intent(this, RepairHistoryActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnAdminManagement).setOnClickListener {
            startActivity(Intent(this, AdminManagementActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnTransactionHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.btnManageService).setOnClickListener {
            startActivity(Intent(this, ServiceManagementActivity::class.java))
        }
    }
}