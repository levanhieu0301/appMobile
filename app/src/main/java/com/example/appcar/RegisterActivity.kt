
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
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtUser = findViewById<EditText>(R.id.edtUser) // Đây là ô nhập Email
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtRePass = findViewById<EditText>(R.id.edtRePass)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val userDAO = UserDAO(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        // 1. Khi click vào nút mũi tên
        btnBack.setOnClickListener {
            finish() // Đóng RegisterActivity, tự động quay về màn hình Login đang chờ bên dưới
        }

        // 2. Khi click vào dòng chữ "Already have an account?"
        txtLogin.setOnClickListener {
            finish() // Tương tự, quay về màn hình Login
        }
        btnRegister.setOnClickListener {

            val fullName = edtName.text.toString().trim() // ✅ thêm
            val email = edtUser.text.toString().trim()
            val p = edtPass.text.toString().trim()
            val rp = edtRePass.text.toString().trim()

            // 1. Kiểm tra rỗng
            if (fullName.isEmpty() || email.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Email hợp lệ
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Email tồn tại
            if (userDAO.isEmailExists(email)) {
                Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Mật khẩu khớp
            if (p != rp) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 5. Lưu DB
            val hashed = HashUtil.hash(p)

            userDAO.insert(fullName, email, hashed, "user") // sửa lại

            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
//        btnRegister.setOnClickListener {
//            val email = edtUser.text.toString().trim()
//            val p = edtPass.text.toString().trim()
//            val rp = edtRePass.text.toString().trim()
//
//            // 1. Kiểm tra để trống
//            if (email.isEmpty() || p.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 2. Kiểm tra định dạng Email hợp lệ
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 3. Kiểm tra Email đã tồn tại chưa
//            if (userDAO.isEmailExists(email)) {
//                Toast.makeText(this, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 4. Kiểm tra khớp mật khẩu
//            if (p != rp) {
//                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 5. Mã hóa và lưu
//            val hashed = HashUtil.hash(p)
//            userDAO.insert(email, hashed, "user")
//            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
//            finish()
//        }
    }
}