//
//package com.example.appcar
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Patterns
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.example.appcar.database.UserDAO
//import com.example.appcar.utils.HashUtil
//
//class LoginActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        val edtUser = findViewById<EditText>(R.id.edtUser)
//        val edtPass = findViewById<EditText>(R.id.edtPass)
//        val btnLogin = findViewById<Button>(R.id.btnLogin)
//        val txtRegister = findViewById<TextView>(R.id.txtRegister)
//        val userDAO = UserDAO(this)
//        // ForgotPass
//        // 1. Ánh xạ View
//        val txtForgotPass = findViewById<TextView>(R.id.txtForgotPass)
//
//        // 2. Thiết lập sự kiện click
//        txtForgotPass.setOnClickListener {
//            val intent = Intent(this, ForgotPasswordActivity::class.java)
//            startActivity(intent)
//        }
//        // End ForgotPass
//        btnLogin.setOnClickListener {
//            val email = edtUser.text.toString().trim()
//            val passInput = edtPass.text.toString().trim()
//
//            // 1. Kiểm tra định dạng Email
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 2. Lấy thông tin từ DB dựa trên Email
//            val credentials = userDAO.getUserCredentials(email)
//
//            if (credentials != null) {
//                val dbPasswordHash = credentials.first
//                val role = credentials.second
//
//                // 3. Mã hóa pass người dùng gõ vào và so sánh với pass trong DB
//                val hashedInput = HashUtil.hash(passInput)
//
//                if (hashedInput == dbPasswordHash) {
//                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
//
//                    // Chuyển trang dựa trên Role
//                    if (role.lowercase() == "admin") {
//                        val intent = Intent(this, AdminActivity::class.java)
//                        intent.putExtra("EMAIL", email)
//                        startActivity(intent)
//
//                    } else {
//                        val intent = Intent(this, HomeActivity::class.java)
//                        intent.putExtra("EMAIL", email)
//                        startActivity(intent)
//                    }
//                    finish()
//                } else {
//                    Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Email này chưa được đăng ký", Toast.LENGTH_SHORT).show()
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

        //  Ánh xạ view
        val edtEmail = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val txtForgotPass = findViewById<TextView>(R.id.txtForgotPass)

        val userDAO = UserDAO(this)

        //  Quên mật khẩu
        txtForgotPass.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Chuyển sang đăng ký
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //  Xử lý đăng nhập
        btnLogin.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val passInput = edtPass.text.toString().trim()


            // 1. Kiểm tra rỗng
            if (email.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Kiểm tra email hợp lệ
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Lấy thông tin từ DB
            val credentials = userDAO.getUserCredentials(email)

            if (credentials != null) {
                val dbPasswordHash = credentials.first
                val role = credentials.second

                // 4. Hash password nhập vào
                val hashedInput = HashUtil.hash(passInput)

                // 5. So sánh password
                if (hashedInput == dbPasswordHash) {
                    // Lấy userId từ email
                    val userId = userDAO.getUserIdByEmail(email)
                    if (userId != null) {
                        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putInt("userId", userId).apply()
                    }
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                    //  THÊM EMAIL Ở ĐÂY
                    if (role.lowercase() == "admin") {
                        val intent = Intent(this, AdminActivity::class.java)
                        intent.putExtra("EMAIL", email) // thêm dòng này
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("EMAIL", email) //thêm dòng này
                        startActivity(intent)
                    }

                    finish()
                } else {
                    Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show()
            }
        }
    }
}