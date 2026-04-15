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
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setTitle("Chỉnh sửa thông tin")

        val view = layoutInflater.inflate(R.layout.dialog_edit, null)
        builder.setView(view)

        val edtFullName = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDialogFullName)
        val edtUsername = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDialogUsername)
        val edtRole = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDialogRole)

        edtFullName.setText(user.fullName)
        edtUsername.setText(user.username)
        edtRole.setText(user.role)

        builder.setPositiveButton("Cập nhật") { dialog, _ ->
            val newFullName = edtFullName.text.toString().trim()
            val newUsername = edtUsername.text.toString().trim()
            val newRole = edtRole.text.toString().trim()

            if (newFullName.isNotEmpty() && newUsername.isNotEmpty()) {
                val result = userDao.updateUser(user.id, newFullName, newUsername, newRole)

                if (result > 0) {
                    loadData()
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ Tên và Email!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }

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