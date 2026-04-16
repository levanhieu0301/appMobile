
package com.example.appcar

import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.UserDAO
import com.example.appcar.utils.HashUtil

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 🔹 Ánh xạ view
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val edtAddress = findViewById<EditText>(R.id.edtAddress)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtRePass = findViewById<EditText>(R.id.edtRePass)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        val userDAO = UserDAO(this)

        // Quay lại login
        btnBack.setOnClickListener { finish() }
        txtLogin.setOnClickListener { finish() }

        //  Xử lý đăng ký
        btnRegister.setOnClickListener {

            val fullName = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val username = edtUsername.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()
            val password = edtPass.text.toString().trim()
            val rePassword = edtRePass.text.toString().trim()
            // Check số điện thoại
//            if (!isValidPhone(phone)) {
//                Toast.makeText(this, "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            if (!isValidPhone(phone)) {
                edtPhone.error = "SĐT không hợp lệ"
                return@setOnClickListener
            }
            // Check mật khẩu
            if (!isValidPassword(password)) {
                Toast.makeText(this, "Mật khẩu phải ≥6 ký tự, có chữ và số", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidPassword(password)) {
                edtPass.error = "Mật khẩu yếu"
                return@setOnClickListener
            }
            //  1. Kiểm tra rỗng
            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty()
                || phone.isEmpty() || address.isEmpty()
                || password.isEmpty() || rePassword.isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Email hợp lệ
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  3. Check email tồn tại
            if (userDAO.isEmailExists(email)) {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  4. Check password
            if (password != rePassword) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  5. Validate số điện thoại
            if (phone.length < 10) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  6. Hash password
            val hashedPassword = HashUtil.hash(password)

            //  7. Lưu DB
            userDAO.insertFull(
                fullName,
                username,
                email,
                phone,
                address,
                hashedPassword,
                "user"
            )

            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun isValidPhone(phone: String): Boolean {
        return Regex("^0[0-9]{9}$").matches(phone)
    }

    private fun isValidPassword(password: String): Boolean {
        return Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$").matches(password)
    }
}