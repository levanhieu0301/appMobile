package com.example.appcar

import android.os.Bundle
import android.widget.ImageButton
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
            onEditClick = { /* Có thể khóa chức năng sửa thông tin khách hàng */ }
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
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                // Nếu bạn có thêm cột full_name trong DB, hãy lấy thêm tại đây
                customerList.add(User(id, name, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
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