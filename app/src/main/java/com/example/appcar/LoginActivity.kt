package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.UserDAO
import com.example.appcar.utils.HashUtil

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtUser = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val userDAO = UserDAO(this)

        // --- Tự động tạo tài khoản Admin để test ---
        if (!userDAO.isEmailExists("admin@gmail.com")) {
            userDAO.insert("Quản trị viên", "admin@gmail.com", HashUtil.hash("123456"), "admin")
        }
        // ------------------------------------------

        // ForgotPass
        val txtForgotPass = findViewById<TextView>(R.id.txtForgotPass)
        txtForgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        
        btnLogin.setOnClickListener {
            val email = edtUser.text.toString().trim()
            val passInput = edtPass.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credentials = userDAO.getUserCredentials(email)

            if (credentials != null) {
                val dbPasswordHash = credentials.first
                val role = credentials.second
                val hashedInput = HashUtil.hash(passInput)

                if (hashedInput == dbPasswordHash) {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                    if (role.lowercase() == "admin") {
                        // Chuyển trực tiếp đến DashboardActivity để tiện quản lý
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Email này chưa được đăng ký", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}