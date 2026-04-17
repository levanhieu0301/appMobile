package com.example.appcar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.BookingAdapter
import com.example.appcar.database.Booking
import com.example.appcar.database.BookingDAO
import com.example.appcar.database.PromotionDAO
import com.example.appcar.database.UserDAO
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var actvServiceType: AutoCompleteTextView
    private lateinit var edtBookingDate: EditText
    private lateinit var edtPromoCode: EditText
    private lateinit var tilPromoCode: TextInputLayout
    private lateinit var btnSubmit: Button
    private lateinit var rvBookings: RecyclerView
    private lateinit var bookingDAO: BookingDAO
    private lateinit var userDAO: UserDAO
    private lateinit var promotionDAO: PromotionDAO
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()
    private var currentUserId: Int = -1

    private val serviceTypes = arrayOf("Thay dầu động cơ", "Rửa xe", "Kiểm tra phanh", "Cân bằng lốp", "Bảo dưỡng tổng thể")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        actvServiceType = findViewById(R.id.actvServiceType)
        edtBookingDate = findViewById(R.id.edtBookingDate)
        edtPromoCode = findViewById(R.id.edtPromoCode)
        tilPromoCode = findViewById(R.id.tilPromoCode)
        btnSubmit = findViewById(R.id.btnSubmitBooking)
        rvBookings = findViewById(R.id.rvBookings)

        bookingDAO = BookingDAO(this)
        userDAO = UserDAO(this)
        promotionDAO = PromotionDAO(this)

        // Lấy user id từ email đã lưu
        val sharedPref = getSharedPreferences("AppPref", MODE_PRIVATE)
        val userEmail = sharedPref.getString("user_email", null)
        if (userEmail != null) {
            currentUserId = userDAO.getUserIdByEmail(userEmail) ?: -1
        }
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup AutoCompleteTextView cho dịch vụ
        val adapterService = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, serviceTypes)
        actvServiceType.setAdapter(adapterService)
        actvServiceType.threshold = 0
        actvServiceType.setOnClickListener { actvServiceType.showDropDown() }

        // Date picker
        edtBookingDate.isFocusable = false
        edtBookingDate.isClickable = true
        edtBookingDate.setOnClickListener { showDatePicker() }

        // Icon chọn mã khuyến mãi
        tilPromoCode.setEndIconOnClickListener {
            showPromotionDialog()
        }

        btnSubmit.setOnClickListener { createBooking() }

        // RecyclerView
        rvBookings.layoutManager = LinearLayoutManager(this)
        adapter = BookingAdapter(bookingList) { booking ->
            cancelBooking(booking)
        }
        rvBookings.adapter = adapter

        loadBookings()
    }

    private fun showPromotionDialog() {
        val promotions = promotionDAO.getAll().filter { 
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
            it.isActive && it.expiryDate >= today && it.usedCount < it.usageLimit
        }

        if (promotions.isEmpty()) {
            Toast.makeText(this, "Hiện không có mã khuyến mãi nào khả dụng", Toast.LENGTH_SHORT).show()
            return
        }

        val promoItems = promotions.map { "${it.code} (Giảm ${it.discountPercent}%)" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn mã khuyến mãi")
            .setItems(promoItems) { _, which ->
                val selectedPromo = promotions[which]
                edtPromoCode.setText(selectedPromo.code)
                Toast.makeText(this, "Đã chọn mã: ${selectedPromo.code}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            edtBookingDate.setText(selectedDate)
        }, year, month, day).show()
    }

    private fun createBooking() {
        val service = actvServiceType.text.toString().trim()
        val date = edtBookingDate.text.toString().trim()
        val promoCode = edtPromoCode.text.toString().trim().uppercase()

        if (service.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ và ngày", Toast.LENGTH_SHORT).show()
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        if (date < today) {
            Toast.makeText(this, "Ngày đặt lịch không được nhỏ hơn hôm nay", Toast.LENGTH_SHORT).show()
            return
        }

        // Xử lý mã khuyến mãi nếu có nhập
        if (promoCode.isNotEmpty()) {
            val promotion = promotionDAO.getPromotionByCode(promoCode)
            if (promotion == null) {
                Toast.makeText(this, "Mã khuyến mãi không hợp lệ", Toast.LENGTH_SHORT).show()
                return
            }

            if (promotion.expiryDate < today) {
                Toast.makeText(this, "Mã khuyến mãi đã hết hạn", Toast.LENGTH_SHORT).show()
                return
            }

            if (promotion.usedCount >= promotion.usageLimit) {
                Toast.makeText(this, "Mã khuyến mãi đã hết lượt sử dụng", Toast.LENGTH_SHORT).show()
                return
            }

            promotionDAO.updateUsedCount(promoCode)
            
            val updatedPromotion = promotionDAO.getPromotionByCode(promoCode)
            if (updatedPromotion != null && updatedPromotion.usedCount >= updatedPromotion.usageLimit) {
                promotionDAO.deleteById(updatedPromotion.id)
            }
            
            Toast.makeText(this, "Đã áp dụng mã giảm giá ${promotion.discountPercent}%", Toast.LENGTH_SHORT).show()
        }

        val booking = Booking(
            userId = currentUserId,
            serviceType = service,
            bookingDate = date,
            status = "PENDING",
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        )
        val id = bookingDAO.insert(booking)
        if (id != -1L) {
            Toast.makeText(this, "Đặt lịch thành công, chờ admin xác nhận", Toast.LENGTH_SHORT).show()
            actvServiceType.setText("")
            edtBookingDate.setText("")
            edtPromoCode.setText("")
            loadBookings()
        } else {
            Toast.makeText(this, "Đặt lịch thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBookings() {
        bookingList.clear()
        bookingList.addAll(bookingDAO.getBookingsByUser(currentUserId))
        adapter.notifyDataSetChanged()
    }

    private fun cancelBooking(booking: Booking) {
        if (booking.status == "COMPLETED") {
            Toast.makeText(this, "Không thể hủy lịch đã hoàn thành", Toast.LENGTH_SHORT).show()
            return
        }
        val deleted = bookingDAO.deleteBooking(booking.id)
        if (deleted) {
            Toast.makeText(this, "Đã hủy lịch", Toast.LENGTH_SHORT).show()
            loadBookings()
        } else {
            Toast.makeText(this, "Hủy lịch thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookings()
    }
}