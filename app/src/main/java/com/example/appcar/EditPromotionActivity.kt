package com.example.appcar

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.PromotionDAO
import java.util.Calendar

class EditPromotionActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText
    private lateinit var edtPercent: EditText
    private lateinit var edtExpiry: EditText
    private lateinit var edtLimit: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack: ImageButton
    private lateinit var dao: PromotionDAO

    private var promotionId: Int = -1
    private var originalCode: String = ""

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_promotion)

        edtCode = findViewById(R.id.edtCode)
        edtPercent = findViewById(R.id.edtPercent)
        edtExpiry = findViewById(R.id.edtExpiry)
        edtLimit = findViewById(R.id.edtLimit)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)
        btnBack = findViewById(R.id.btnBack)
        dao = PromotionDAO(this)

        promotionId = intent.getIntExtra("promotion_id", -1)
        if (promotionId == -1) {
            Toast.makeText(this, "Lỗi: không tìm thấy mã", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadPromotionData()

        edtExpiry.isFocusable = false
        edtExpiry.isClickable = true
        edtExpiry.setOnClickListener {
            showDatePicker()
        }

        btnUpdate.setOnClickListener {
            updatePromotion()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadPromotionData() {
        val allPromos = dao.getAll()
        val promotion = allPromos.find { it.id == promotionId }
        if (promotion == null) {
            Toast.makeText(this, "Không tìm thấy mã khuyến mãi", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        originalCode = promotion.code
        edtCode.setText(promotion.code)
        edtPercent.setText(promotion.discountPercent.toString())
        edtExpiry.setText(promotion.expiryDate)
        edtLimit.setText(promotion.usageLimit.toString())

        // Parse ngày từ expiryDate
        try {
            val parts = promotion.expiryDate.split("-")
            if (parts.size == 3) {
                selectedYear = parts[0].toInt()
                selectedMonth = parts[1].toInt() - 1
                selectedDay = parts[2].toInt()
            } else {
                val calendar = Calendar.getInstance()
                selectedYear = calendar.get(Calendar.YEAR)
                selectedMonth = calendar.get(Calendar.MONTH)
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            }
        } catch (e: Exception) {
            val calendar = Calendar.getInstance()
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH)
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month
                selectedDay = dayOfMonth
                edtExpiry.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth))
            },
            selectedYear, selectedMonth, selectedDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun updatePromotion() {
        val code = edtCode.text.toString().trim().uppercase()
        val percentStr = edtPercent.text.toString().trim()
        val expiry = edtExpiry.text.toString().trim()
        val limitStr = edtLimit.text.toString().trim()

        if (code.isEmpty() || percentStr.isEmpty() || expiry.isEmpty() || limitStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }
        val percent = percentStr.toIntOrNull()
        val limit = limitStr.toIntOrNull()
        if (percent == null || percent !in 0..100) {
            Toast.makeText(this, "Phần trăm phải từ 0 đến 100", Toast.LENGTH_SHORT).show()
            return
        }
        if (limit == null || limit <= 0) {
            Toast.makeText(this, "Giới hạn sử dụng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày
        val today = Calendar.getInstance()
        val selectedCal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }
        if (selectedCal.before(today) && !isSameDay(selectedCal, today)) {
            Toast.makeText(this, "Ngày hết hạn không được trước hôm nay", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra code trùng (trừ chính nó)
        val existing = dao.getPromotionByCode(code)
        if (existing != null && existing.id != promotionId) {
            Toast.makeText(this, "Mã $code đã tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        // Cập nhật vào database
        val success = dao.update(
            id = promotionId,
            code = code,
            discountPercent = percent,
            expiryDate = expiry,
            usageLimit = limit
        )
        if (success) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}