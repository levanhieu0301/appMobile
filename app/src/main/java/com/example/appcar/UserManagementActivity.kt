package com.example.appcar

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.AdminAdapter
import com.example.appcar.adapter.User
import com.example.appcar.database.UserDAO

class UserManagementActivity : AppCompatActivity() {

    private lateinit var userDao: UserDAO
    private lateinit var adapter: AdminAdapter
    private var customerList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        userDao = UserDAO(this)
        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        val btnBack = findViewById<ImageButton>(R.id.btnBackUser)

        adapter = AdminAdapter(customerList,
            onDeleteClick = { user -> confirmDelete(user) },
            onEditClick = { user -> showEditUserDialog(user) }
        )

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        loadData()
        btnBack.setOnClickListener { finish() }
    }

    private fun loadData() {
        customerList.clear()
        val cursor = userDao.getAllUsers()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")) // Lấy thêm cột này
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

                customerList.add(User(id, username, fullName, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun showEditUserDialog(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chỉnh sửa khách hàng")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val edtFullName = EditText(this).apply {
            hint = "Họ và tên khách hàng"
            setText(user.fullName)
        }

        val edtUsername = EditText(this).apply {
            hint = "Tên đăng nhập/Email"
            setText(user.username)
        }

        val edtRole = EditText(this).apply {
            hint = "Vai trò (user/admin)"
            setText(user.role)
        }

        layout.addView(edtFullName)
        layout.addView(edtUsername)
        layout.addView(edtRole)
        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val newFullName = edtFullName.text.toString().trim()
            val newUsername = edtUsername.text.toString().trim()
            val newRole = edtRole.text.toString().trim()

            if (newFullName.isNotEmpty() && newUsername.isNotEmpty() && newRole.isNotEmpty()) {
                val rows = userDao.updateUser(user.id, newFullName, newUsername, newRole)
                if (rows > 0) {
                    loadData()
                    Toast.makeText(this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Không được để trống!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Xóa khách hàng")
            .setMessage("Bạn có chắc chắn muốn xóa tài khoản ${user.username}?")
            .setPositiveButton("Xóa") { _, _ ->
                userDao.deleteUser(user.id)
                loadData()
                Toast.makeText(this, "Đã xóa người dùng", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}