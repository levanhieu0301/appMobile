package com.example.appcar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.Promotion
import com.example.appcar.database.PromotionDAO

class CreatePromotionActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText
    private lateinit var edtPercent: EditText
    private lateinit var edtExpiry: EditText
    private lateinit var edtLimit: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var dao: PromotionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_promotion)

        edtCode = findViewById(R.id.edtCode)
        edtPercent = findViewById(R.id.edtPercent)
        edtExpiry = findViewById(R.id.edtExpiry)
        edtLimit = findViewById(R.id.edtLimit)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        dao = PromotionDAO(this)

        btnSave.setOnClickListener {
            val code = edtCode.text.toString().trim().uppercase()
            val percentStr = edtPercent.text.toString().trim()
            val expiry = edtExpiry.text.toString().trim()
            val limitStr = edtLimit.text.toString().trim()

            if (code.isEmpty() || percentStr.isEmpty() || expiry.isEmpty() || limitStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val percent = percentStr.toIntOrNull()
            val limit = limitStr.toIntOrNull()
            if (percent == null || percent !in 0..100) {
                Toast.makeText(this, "Phần trăm phải từ 0 đến 100", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (limit == null || limit <= 0) {
                Toast.makeText(this, "Giới hạn sử dụng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dao.getPromotionByCode(code) != null) {
                Toast.makeText(this, "Mã $code đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val promotion = Promotion(
                code = code,
                discountPercent = percent,
                expiryDate = expiry,
                usageLimit = limit,
                usedCount = 0,
                isActive = true
            )
            dao.insert(promotion)
            Toast.makeText(this, "Đã thêm mã khuyến mãi", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}