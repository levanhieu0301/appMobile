package com.example.appcar

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcar.adapter.AppointmentAdapter
import com.example.appcar.database.AppointmentDAO
import com.example.appcar.databinding.ActivityHistoryBinding
import com.example.appcar.model.Appointment

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var appointmentDAO: AppointmentDAO
    private lateinit var adapter: AppointmentAdapter
    private var appointmentList = mutableListOf<Appointment>()
    private var fullList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)

        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        loadData()
    }

    private fun setupRecyclerView() {
        /* Code cũ
        adapter = AppointmentAdapter(appointmentList)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
        */
        
        // Code mới: Hỗ trợ callback Duyệt/Hủy
        adapter = AppointmentAdapter(appointmentList, 
            onConfirmClick = { appointment ->
                updateStatus(appointment.id, "CONFIRMED")
            },
            onCancelClick = { appointment ->
                updateStatus(appointment.id, "CANCELLED")
            }
        )
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun updateStatus(id: Int, status: String) {
        if (appointmentDAO.updateBookingStatus(id, status) > 0) {
            Toast.makeText(this, "Đã cập nhật trạng thái: $status", Toast.LENGTH_SHORT).show()
            loadData()
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAll.setOnClickListener {
            updateTabUI(binding.btnAll)
            loadData()
        }

        binding.btnPending.setOnClickListener {
            updateTabUI(binding.btnPending)
            filterDataByStatus("Chờ xác nhận")
        }

        binding.btnDone.setOnClickListener {
            updateTabUI(binding.btnDone)
            filterDataByStatus("CONFIRMED") // Thay đổi từ "Hoàn thành"
        }

        binding.btnCancel.setOnClickListener {
            updateTabUI(binding.btnCancel)
            filterDataByStatus("CANCELLED") // Thay đổi từ "Đã hủy"
        }
    }

    private fun updateTabUI(selectedTab: TextView) {
        val tabs = listOf(binding.btnAll, binding.btnPending, binding.btnDone, binding.btnCancel)
        for (tab in tabs) {
            tab.setBackgroundColor(Color.TRANSPARENT)
            tab.setTextColor("#546E7A".toColorInt())
            tab.typeface = android.graphics.Typeface.DEFAULT
        }
        selectedTab.setBackgroundColor("#3F51B5".toColorInt())
        selectedTab.setTextColor(Color.WHITE)
        selectedTab.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private fun setupSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /* Code cũ
                val query = s.toString().lowercase()
                val filteredList = fullList.filter { 
                    it.serviceName.lowercase().contains(query) || it.location.lowercase().contains(query)
                }
                appointmentList.clear()
                appointmentList.addAll(filteredList)
                adapter.notifyDataSetChanged()
                */
                
                // Code mới: Tìm kiếm theo hãng xe hoặc dịch vụ
                val query = s.toString().lowercase()
                val filteredList = fullList.filter { 
                    it.carBrand.lowercase().contains(query) || it.services.lowercase().contains(query)
                }
                appointmentList.clear()
                appointmentList.addAll(filteredList)
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadData() {
        /* Code cũ
        appointmentList.clear()
        fullList.clear()
        val cursor = appointmentDAO.getAllAppointments()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val loc = cursor.getString(cursor.getColumnIndexOrThrow("loc"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val price = cursor.getLong(cursor.getColumnIndexOrThrow("price"))

                val item = Appointment(id, name, date, loc, status, price)
                appointmentList.add(item)
                fullList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
        */

        // Code mới: Lấy dữ liệu từ bảng bookings
        appointmentList.clear()
        fullList.clear()
        val cursor = appointmentDAO.getAllAppointmentsFromBookings()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                val carBrand = cursor.getString(cursor.getColumnIndexOrThrow("car_brand"))
                val services = cursor.getString(cursor.getColumnIndexOrThrow("services"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("booking_time"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("final_price"))
                val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))

                val item = Appointment(id, userId, carBrand, services, date, time, status, price, note)
                appointmentList.add(item)
                fullList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun filterDataByStatus(status: String) {
        val filtered = if (status == "Tất cả") fullList else fullList.filter { it.status == status }
        appointmentList.clear()
        appointmentList.addAll(filtered)
        adapter.notifyDataSetChanged()
    }
}
