package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appcar.database.UserDAO
class AdminActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val txtUser = findViewById<TextView>(R.id.txtUser)
        val txtUserSidebar = findViewById<TextView>(R.id.txtUserSidebar)
        val userDAO = UserDAO(this)

        // nhận email từ Login
        val email = intent.getStringExtra("EMAIL")

        if (email != null) {
            val fullName = userDAO.getFullNameByEmail(email)
            txtUser.text = fullName ?: "User"
            txtUserSidebar.text = fullName
        }
        drawerLayout = findViewById(R.id.drawerLayout)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Menu click
        findViewById<TextView>(R.id.menuDashboard).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<TextView>(R.id.menuInfo).setOnClickListener {
            startActivity(Intent(this, AdminManagementActivity::class.java))
        }

        findViewById<TextView>(R.id.menuVoucher).setOnClickListener {
            startActivity(Intent(this, VoucherActivity::class.java))
        }

        findViewById<TextView>(R.id.menuHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<TextView>(R.id.menuLogout).setOnClickListener {

            //Xóa session nếu có (optional)
            val prefs = getSharedPreferences("USER", MODE_PRIVATE)
            prefs.edit().clear().apply()

            // Quay về Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}