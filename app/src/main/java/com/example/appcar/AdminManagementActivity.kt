package com.example.appcar

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
import com.example.appcar.database.User
import com.example.appcar.database.UserDAO
import com.example.appcar.utils.HashUtil
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
    }

    private fun loadData() {
        adminList.clear()
        val cursor = userDAO.getAllAdmins()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                val fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"))

                adminList.add(User(id, name, fullName, role))

            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun showAddAdminDialog() {
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setTitle("Thêm Admin Mới")

        val view = layoutInflater.inflate(R.layout.dialog_add_admin, null)
        builder.setView(view)

        val edtFullName = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddFullName)
        val edtEmail = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddEmail)
        val edtPhone = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddPhone)
        val edtAddress = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddAddress)
        val edtPass = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddPass)

        builder.setPositiveButton("Thêm") { dialog, _ ->
            val fullName = edtFullName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
                if (!userDAO.isEmailExists(email)) {
                    val hashedPass = HashUtil.hash(pass)

                    userDAO.insert(
                        fullName,
                        email,
                        hashedPass,
                        phone,
                        address,
                        "admin"
                    )

                    loadData()
                    Toast.makeText(this, "Đã thêm admin thành công", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Email này đã tồn tại!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showEditDialog(user: User) {
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setTitle("Cập nhật thông tin Admin")

        val view = layoutInflater.inflate(R.layout.dialog_edit_admin, null)
        builder.setView(view)

        val edtEmail = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAdminEmail)
        val edtPass = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAdminPass)

        edtEmail.setText(user.username)

        builder.setPositiveButton("Cập nhật") { dialog, _ ->
            val newEmail = edtEmail.text.toString().trim()
            val newPass = edtPass.text.toString().trim()

            if (newEmail.isNotEmpty()) {
                if (newPass.isNotEmpty()) {
                    val hashedPass = HashUtil.hash(newPass)
                    userDAO.updateAdminWithPass(user.id, newEmail, hashedPass, "admin")
                } else {
                    userDAO.updateAdmin(user.id, newEmail, "admin")
                }
                loadData()
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
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