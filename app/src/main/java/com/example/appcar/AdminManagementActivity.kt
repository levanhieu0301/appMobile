package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.AdminAdapter
import com.example.appcar.adapter.User
import com.example.appcar.database.UserDAO
import com.example.appcar.utils.HashUtil // Nhớ import HashUtil của bạn
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminManagementActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var adapter: AdminAdapter
    private var adminList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_management)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
        userDAO = UserDAO(this)
        val rvAdmin = findViewById<RecyclerView>(R.id.rvAdmin)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddAdmin)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        adapter = AdminAdapter(
            adminList,
            onDeleteClick = { user -> confirmDelete(user) },
            onEditClick = { user -> showEditDialog(user) }
        )
        rvAdmin.layoutManager = LinearLayoutManager(this)
        rvAdmin.adapter = adapter

        loadData()

        fabAdd.setOnClickListener {
            showAddAdminDialog()
        }
//
//        bottomNav.selectedItemId = R.id.nav_admin
//        bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> {
//                    startActivity(Intent(this, DashboardActivity::class.java))
//                    finish()
//                    true
//                }
//                R.id.nav_admin -> true
//                else -> false
//            }
//        }
    }

    private fun loadData() {
        adminList.clear()
        val cursor = userDAO.getAllAdmins()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                adminList.add(User(id, name, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun showAddAdminDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm Admin Mới")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        // FULL NAME
        val edtFullName = EditText(this).apply {
            hint = "Họ và tên"
        }

        val edtEmail = EditText(this).apply {
            hint = "Email đăng nhập"
        }

        val edtPass = EditText(this).apply {
            hint = "Mật khẩu"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(edtFullName)
        layout.addView(edtEmail)
        layout.addView(edtPass)

        builder.setView(layout)

        builder.setPositiveButton("Thêm") { _, _ ->

            val fullName = edtFullName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {

                if (!userDAO.isEmailExists(email)) {

                    val hashedPass = HashUtil.hash(pass)

                    // INSERT ĐÚNG
                    userDAO.insert(
                        fullName,
                        email,
                        hashedPass,
                        "admin"
                    )

                    loadData()
                    Toast.makeText(this, "Đã thêm admin thành công", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    // --- CHỨC NĂNG SỬA (Email + Password) ---
    private fun showEditDialog(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cập nhật thông tin Admin")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val edtEmail = EditText(this).apply { setText(user.username) }
        val edtPass = EditText(this).apply {
            hint = "Mật khẩu mới (Để trống nếu không đổi)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(edtEmail)
        layout.addView(edtPass)
        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val newEmail = edtEmail.text.toString().trim()
            val newPass = edtPass.text.toString().trim()

            if (newEmail.isNotEmpty()) {
                // Nếu người dùng có nhập pass mới thì mã hóa, nếu không thì giữ nguyên pass cũ (cần hàm update tương ứng trong DAO)
                if (newPass.isNotEmpty()) {
                    val hashedPass = HashUtil.hash(newPass)
                    userDAO.updateAdminWithPass(user.id, newEmail, hashedPass, "admin")
                } else {
                    userDAO.updateAdmin(user.id, newEmail, "admin")
                }
                loadData()
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa tài khoản ${user.username}?")
            .setPositiveButton("Xóa") { _, _ ->
                userDAO.deleteUser(user.id)
                loadData()
                Toast.makeText(this, "Đã xóa admin", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Không", null)
            .show()
    }
}