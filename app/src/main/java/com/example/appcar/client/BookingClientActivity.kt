package com.example.appcar.client

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appcar.HomeActivity
import com.example.appcar.R
import com.example.appcar.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class BookingClientActivity : AppCompatActivity() {

    private lateinit var actvCarBrand: AutoCompleteTextView
    private lateinit var edtServices: EditText
    private lateinit var edtBookingDate: EditText
    private lateinit var edtBookingTime: EditText
    private lateinit var edtPromoCode: EditText
    private lateinit var edtNote: EditText
    private lateinit var tilPromoCode: TextInputLayout
    private lateinit var btnSubmit: Button
    
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvDiscountAmount: TextView
    private lateinit var tvFinalPrice: TextView

    private lateinit var bookingDAO: BookingDAO
    private lateinit var userDAO: UserDAO
    private lateinit var promotionDAO: PromotionDAO
    private lateinit var serviceDao: ServiceDao

    private var currentUserId: Int = -1
    private var userEmail: String? = null
    
    private var selectedServiceNames = mutableListOf<String>()
    private var totalPrice = 0.0
    private var finalPrice = 0.0
    private var discountPercent = 0
    private var selectedPromoCode: String? = null

    private val carBrands = arrayOf("Mercedes-Benz", "BMW", "Audi", "Toyota", "Honda", "Hyundai", "Kia", "Mazda", "Ford", "VinFast")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        initViews()
        initData()
        setupListeners()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        actvCarBrand = findViewById(R.id.actvCarBrand)
        edtServices = findViewById(R.id.edtServices)
        edtBookingDate = findViewById(R.id.edtBookingDate)
        edtBookingTime = findViewById(R.id.edtBookingTime)
        edtPromoCode = findViewById(R.id.edtPromoCode)
        edtNote = findViewById(R.id.edtNote)
        tilPromoCode = findViewById(R.id.tilPromoCode)
        btnSubmit = findViewById(R.id.btnSubmitBooking)
        
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount)
        tvFinalPrice = findViewById(R.id.tvFinalPrice)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_booking
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { navigate(this, HomeActivity::class.java); true }
                R.id.nav_booking -> true
                R.id.nav_history -> { navigate(this, HistoryClientActivity::class.java); true }
                R.id.nav_account -> { navigate(this, ProfileClientActivity::class.java); true }
                else -> false
            }
        }
    }

    private fun initData() {
        bookingDAO = BookingDAO(this)
        userDAO = UserDAO(this)
        promotionDAO = PromotionDAO(this)
        serviceDao = ServiceDao(this)

        val sharedPref = getSharedPreferences("AppPref", MODE_PRIVATE)
        userEmail = sharedPref.getString("user_email", null)
        if (userEmail != null) {
            currentUserId = userDAO.getUserIdByEmail(userEmail!!) ?: -1
        }
        
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            finish()
        }

        val brandAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, carBrands)
        actvCarBrand.setAdapter(brandAdapter)
    }

    private fun setupListeners() {
        actvCarBrand.setOnClickListener { actvCarBrand.showDropDown() }
        edtServices.setOnClickListener { showMultiServiceDialog() }
        edtBookingDate.setOnClickListener { showDatePicker() }
        edtBookingTime.setOnClickListener { showTimePicker() }

        // Nhấn vào dấu cộng (+) để hiện danh sách mã khuyến mãi
        tilPromoCode.setEndIconOnClickListener { showPromotionListDialog() }

        btnSubmit.setOnClickListener { createBooking() }
    }

    private fun showPromotionListDialog() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        // Lấy danh sách mã còn hiệu lực từ Database
        val allPromos = promotionDAO.getAll().filter { 
            it.isActive && it.expiryDate >= today && it.usedCount < it.usageLimit 
        }

        if (allPromos.isEmpty()) {
            Toast.makeText(this, "Hiện không có mã khuyến mãi nào khả dụng", Toast.LENGTH_SHORT).show()
            return
        }

        val promoItems = allPromos.map { "${it.code} (Giảm ${it.discountPercent}%)" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn mã khuyến mãi")
            .setItems(promoItems) { _, which ->
                val selectedPromo = allPromos[which]
                edtPromoCode.setText(selectedPromo.code)
                discountPercent = selectedPromo.discountPercent
                selectedPromoCode = selectedPromo.code // Lưu mã đã chọn
                Toast.makeText(this, "Đã áp dụng mã: ${selectedPromo.code}", Toast.LENGTH_SHORT).show()
                calculatePrices() // Tính lại tiền ngay lập tức
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showMultiServiceDialog() {
        val servicesFromDb = serviceDao.getAllServicesList()
        if (servicesFromDb.isEmpty()) {
            Toast.makeText(this, "Hiện không có dịch vụ nào", Toast.LENGTH_SHORT).show()
            return
        }
        
        val names = servicesFromDb.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(names.size) { index -> 
            selectedServiceNames.contains(names[index]) 
        }

        AlertDialog.Builder(this)
            .setTitle("Chọn các dịch vụ")
            .setMultiChoiceItems(names, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    if (!selectedServiceNames.contains(names[which])) selectedServiceNames.add(names[which])
                } else {
                    selectedServiceNames.remove(names[which])
                }
            }
            .setPositiveButton("Xác nhận") { _, _ ->
                edtServices.setText(selectedServiceNames.joinToString(", "))
                calculatePrices()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun calculatePrices() {
        val allServices = serviceDao.getAllServicesList()
        // Tính tổng tiền dựa trên các tên dịch vụ đã chọn
        totalPrice = allServices.filter { selectedServiceNames.contains(it.name) }
            .sumOf { 
                // Xử lý chuỗi giá (ví dụ "150.000đ" -> 150000.0)
                it.priceText.replace(Regex("[^0-9]"), "").toDoubleOrNull() ?: 0.0
            }

        val discount = totalPrice * (discountPercent / 100.0)
        finalPrice = totalPrice - discount

        tvTotalPrice.text = "Tổng tiền dịch vụ: ${String.format("%,.0f", totalPrice)}đ"
        tvDiscountAmount.text = "Giảm giá ($discountPercent%): ${String.format("%,.0f", discount)}đ"
        tvFinalPrice.text = "Thành tiền: ${String.format("%,.0f", finalPrice)}đ"
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            edtBookingDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, h, min ->
            edtBookingTime.setText(String.format("%02d:%02d", h, min))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun createBooking() {
        val brand = actvCarBrand.text.toString()
        val services = edtServices.text.toString()
        val date = edtBookingDate.text.toString()
        val time = edtBookingTime.text.toString()
        val note = "Hãng xe: $brand\n" + edtNote.text.toString()
        val phone = userDAO.getUserPhoneByEmail(userEmail ?: "")
        val promoCode = edtPromoCode.text.toString().trim()

        if (brand.isEmpty() || services.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val booking = Booking(
            userId = currentUserId,
            name = "",
            phone = phone,
            serviceType = services,
            bookingDate = date,
            time = time,
            loc = "Tại trung tâm",
            note = note,
            status = "PENDING",
            price = finalPrice,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )

        if (bookingDAO.insert(booking) != -1L) {
            // 🔥 CẬP NHẬT SỐ LẦN SỬ DỤNG MÃ KHUYẾN MÃI
            if (promoCode.isNotEmpty()) {
                promotionDAO.updateUsedCount(promoCode)
            }

            Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_LONG).show()
            
            // Chuyển sang trang Lịch sử sau khi đặt thành công
            val intent = Intent(this, HistoryClientActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Lỗi khi lưu lịch hẹn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigate(activity: Activity, target: Class<*>) {
        val intent = Intent(activity, target)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
