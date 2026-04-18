package com.example.appcar.client

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.HomeActivity
import com.example.appcar.R
import com.google.android.material.bottomnavigation.BottomNavigationView

import android.app.AlertDialog
import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.appcar.LoginActivity
import com.example.appcar.database.AppDatabase
import com.example.appcar.database.UserDAO
import com.example.appcar.database.User
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.appcar.utils.HashUtil

class ProfileClientActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnEditMode: ImageButton
    private lateinit var btnSaveInfo: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnChangeAvatar: Button

    private var currentUserId: Int = -1
    private lateinit var userDao: UserDAO
    private var isEditMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userDao = UserDAO(this)

        // Lấy userId từ SharedPreferences
        currentUserId = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, com.example.appcar.LoginActivity::class.java))
            finish()
            return
        }

        // Ánh xạ view
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        btnEditMode = findViewById(R.id.btnEditMode)
        btnSaveInfo = findViewById(R.id.btnSaveInfo)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar)

        // Load thông tin user
        loadUserInfo()

        // Nút chỉnh sửa: bật chế độ edit
        btnEditMode.setOnClickListener {
            enableEditMode(true)
        }

        // Nút lưu: cập nhật và tắt chế độ edit
        btnSaveInfo.setOnClickListener {
            saveUserInfo()
        }

        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnChangeAvatar.setOnClickListener {
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_account

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

                R.id.nav_history -> {
                    navigate(this, HistoryClientActivity::class.java)
                    true
                }

                R.id.nav_account -> true

                else -> false
            }
        }
    }

    private fun loadUserInfo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByIdAsObject(currentUserId)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    etUsername.setText(user.username ?: "")
                    etEmail.setText(user.email ?: "")
                    etPhone.setText(user.phone ?: "")
                    etAddress.setText(user.address ?: "")
                    // Load avatar nếu có
                } else {
                    Toast.makeText(this@ProfileClientActivity, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enableEditMode(enable: Boolean) {
        isEditMode = enable
        etUsername.isEnabled = enable
        etEmail.isEnabled = enable
        etPhone.isEnabled = enable
        etAddress.isEnabled = enable
        btnSaveInfo.visibility = if (enable) View.VISIBLE else View.GONE
        btnEditMode.visibility = if (enable) View.GONE else View.VISIBLE
        // Khi bật edit mode, focus vào trường đầu tiên
        if (enable) etUsername.requestFocus()
    }

    private fun saveUserInfo() {
        val newUsername = etUsername.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val newPhone = etPhone.text.toString().trim()
        val newAddress = etAddress.text.toString().trim()

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Tên và email không được để trống", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val result = userDao.updateUserBasicInfo(currentUserId, newUsername, newEmail, newPhone, newAddress)
            withContext(Dispatchers.Main) {
                if (result > 0) {
                    Toast.makeText(this@ProfileClientActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    enableEditMode(false) // Tắt chế độ chỉnh sửa
                } else {
                    Toast.makeText(this@ProfileClientActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etOldPass = dialogView.findViewById<EditText>(R.id.etOldPassword)
        val etNewPass = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPass = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Đổi mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { _, _ ->
                val oldPassword = etOldPass.text.toString()
                val newPassword = etNewPass.text.toString()
                val confirmPassword = etConfirmPass.text.toString()

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (newPassword.length < 4) {
                    Toast.makeText(this, "Mật khẩu phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    val user = userDao.getUserByIdAsObject(currentUserId)
                    if (user != null && user.password == HashUtil.hash(oldPassword)) {
                        val hashedNew = HashUtil.hash(newPassword)
                        val result = userDao.updatePassword(currentUserId, hashedNew)
                        withContext(Dispatchers.Main) {
                            if (result > 0) {
                                Toast.makeText(
                                    this@ProfileClientActivity,
                                    "Đổi mật khẩu thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ProfileClientActivity,
                                    "Đổi mật khẩu thất bại",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ProfileClientActivity,
                                "Mật khẩu cũ không đúng",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun logout() {
        // Xóa userId khỏi SharedPreferences
        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().remove("userId").apply()
        // Xóa toàn bộ dữ liệu session nếu cần (ví dụ: xóa token)
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        // Chuyển về LoginActivity và đóng tất cả activity phía trước
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun navigate(activity: Activity, target: Class<*>) {
        val intent = Intent(activity, target)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
