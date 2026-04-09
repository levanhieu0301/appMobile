package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 1. Ánh xạ Bottom Navigation View
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 2. Xử lý sự kiện khi click vào các mục trên Menu
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Đang ở Trang chủ", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_service -> {
                    Toast.makeText(this, "Dịch vụ xe", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_history -> {
                    Toast.makeText(this, "Lịch sử giao dịch", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_admin -> {
                    // Chuyển sang trang Quản lý Admin đã tạo trước đó
                    val intent = Intent(this, AdminManagementActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // 3. Giữ lại chức năng Logout (Nếu bạn vẫn để nút này trong layout)
        findViewById<Button>(R.id.btnLogoutAdmin)?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}