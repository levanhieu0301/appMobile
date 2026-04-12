package com.example.appcar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentDAO = AppointmentDAO(this)

        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(appointmentList)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupClickListeners() {
        // Quay lại
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Lọc
        binding.btnAll.setOnClickListener {
            loadData()
            Toast.makeText(this, "Hiển thị tất cả", Toast.LENGTH_SHORT).show()
        }

        // Lọc: Hoàn thành (Khớp ID btnDone)
        binding.btnDone.setOnClickListener {
            filterData("Hoàn thành")
        }

        // Lọc: Đã hủy (Khớp ID btnCancel)
        binding.btnCancel.setOnClickListener {
            filterData("Đã hủy")
        }

    }

    private fun loadData() {
        appointmentList.clear()
        val cursor = appointmentDAO.getAllAppointments()
        readCursor(cursor)
    }

    private fun filterData(status: String) {
        appointmentList.clear()
        val cursor = appointmentDAO.getAppointmentsByStatus(status)
        readCursor(cursor)
        Toast.makeText(this, "Đã lọc: $status", Toast.LENGTH_SHORT).show()
    }

    private fun readCursor(cursor: android.database.Cursor) {
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val loc = cursor.getString(cursor.getColumnIndexOrThrow("loc"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val price = cursor.getLong(cursor.getColumnIndexOrThrow("price"))

                // Truyền vào đúng thứ tự của data class Appointment
                appointmentList.add(Appointment(id, name, date, loc, status, price))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }
}