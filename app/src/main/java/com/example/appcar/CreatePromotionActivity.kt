package com.example.appcar

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.database.Promotion
import com.example.appcar.database.PromotionDAO
import java.util.Calendar

class CreatePromotionActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText
    private lateinit var edtPercent: EditText
    private lateinit var edtExpiry: EditText
    private lateinit var edtLimit: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack: ImageButton
    private lateinit var dao: PromotionDAO

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_promotion)

        edtCode = findViewById(R.id.edtCode)
        edtPercent = findViewById(R.id.edtPercent)
        edtExpiry = findViewById(R.id.edtExpiry)
        edtLimit = findViewById(R.id.edtLimit)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnBack = findViewById(R.id.btnBack)
        dao = PromotionDAO(this)

        // Khởi tạo ngày hiện tại
        val calendar = Calendar.getInstance()
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Hiển thị ngày mặc định (hôm nay) lên EditText
        edtExpiry.setText(String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay))
        edtExpiry.isFocusable = false
        edtExpiry.isClickable = true

        edtExpiry.setOnClickListener {
            showDatePicker()
        }

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

            // Kiểm tra ngày không được trước hôm nay
            val today = Calendar.getInstance()
            val selectedCal = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            if (selectedCal.before(today) && !isSameDay(selectedCal, today)) {
                Toast.makeText(this, "Ngày hết hạn không được trước hôm nay", Toast.LENGTH_SHORT).show()
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

        btnBack.setOnClickListener {
            finish()
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
        // Giới hạn không cho chọn ngày trong quá khứ
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}