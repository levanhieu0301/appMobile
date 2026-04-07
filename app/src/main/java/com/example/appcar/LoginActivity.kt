//package com.example.appcar
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.example.appcar.database.UserDAO
//import com.example.appcar.utils.HashUtil
//
//class LoginActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        val edtUser = findViewById<EditText>(R.id.edtUser)
//        val edtPass = findViewById<EditText>(R.id.edtPass)
//        val btnLogin = findViewById<Button>(R.id.btnLogin)
//        val txtRegister = findViewById<TextView>(R.id.txtRegister)
//
//        val userDAO = UserDAO(this)
//
//        btnLogin.setOnClickListener {
//            val u = edtUser.text.toString()
//            val p = edtPass.text.toString()
//
//            val role = userDAO.loginAndGetRole(u, p) // trả về role
//
//            if (role != null) {
//                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
//
//                if (role == "admin") {
//                    Toast.makeText(this, "Admin", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "User", Toast.LENGTH_SHORT).show()
//                }
//
//            } else {
//                Toast.makeText(this, "Sai tài khoản", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        txtRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//    }
//}
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
        // ForgotPass
        // 1. Ánh xạ View
        val txtForgotPass = findViewById<TextView>(R.id.txtForgotPass)

        // 2. Thiết lập sự kiện click
        txtForgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            // Lưu ý: Không dùng finish() ở đây vì người dùng có thể muốn quay lại màn hình Login
        }
        // End ForgotPass
        btnLogin.setOnClickListener {
            val email = edtUser.text.toString().trim()
            val passInput = edtPass.text.toString().trim()

            // 1. Kiểm tra định dạng Email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Lấy thông tin từ DB dựa trên Email
            val credentials = userDAO.getUserCredentials(email)

            if (credentials != null) {
                val dbPasswordHash = credentials.first
                val role = credentials.second

                // 3. Mã hóa pass người dùng gõ vào và so sánh với pass trong DB
                val hashedInput = HashUtil.hash(passInput)

                if (hashedInput == dbPasswordHash) {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                    // Chuyển trang dựa trên Role
                    if (role.lowercase() == "admin") {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, HomeActivity::class.java))
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
