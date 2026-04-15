package com.example.appcar

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
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
        adapter = AppointmentAdapter(appointmentList)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAll.setOnClickListener {
            updateTabUI(binding.btnAll)
            loadData()
        }

        binding.btnDone.setOnClickListener {
            updateTabUI(binding.btnDone)
            filterDataByStatus("Hoàn thành")
        }

        binding.btnCancel.setOnClickListener {
            updateTabUI(binding.btnCancel)
            filterDataByStatus("Đã hủy")
        }
    }

    private fun updateTabUI(selectedTab: TextView) {
        // Reset tất cả tab về trạng thái chưa chọn
        val tabs = listOf(binding.btnAll, binding.btnDone, binding.btnCancel)
        for (tab in tabs) {
            tab.setBackgroundColor(Color.TRANSPARENT)
            tab.setTextColor(Color.parseColor("#546E7A"))
        }
        // Highlight tab được chọn
        selectedTab.setBackgroundColor(Color.parseColor("#3F51B5"))
        selectedTab.setTextColor(Color.WHITE)
    }

    private fun setupSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = fullList.filter { 
                    it.serviceName.lowercase().contains(query) || it.location.lowercase().contains(query)
                }
                appointmentList.clear()
                appointmentList.addAll(filteredList)
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadData() {
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
    }

    private fun filterDataByStatus(status: String) {
        val filtered = fullList.filter { it.status == status }
        appointmentList.clear()
        appointmentList.addAll(filtered)
        adapter.notifyDataSetChanged()
    }
}