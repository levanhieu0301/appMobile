package com.example.appcar

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val btnBack = findViewById<ImageButton>(R.id.btnBackForgot)
        btnBack.setOnClickListener {
            finish() // Quay lại màn hình Login
        }
    }
}