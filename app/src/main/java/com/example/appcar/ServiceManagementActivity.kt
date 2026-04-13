package com.example.appcar

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.MaintenanceService
import com.example.appcar.adapter.ServiceAdapter
import com.example.appcar.database.ServiceDao
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ServiceManagementActivity : AppCompatActivity() {

    private lateinit var serviceDao: ServiceDao
    private lateinit var adapter: ServiceAdapter
    private var serviceList = mutableListOf<MaintenanceService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_management)

        serviceDao = ServiceDao(this)
        val rvServices = findViewById<RecyclerView>(R.id.rvServices)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddService)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        adapter = ServiceAdapter(serviceList,
            onDeleteClick = { service -> confirmDelete(service) },
            onEditClick = { service -> showServiceDialog(service) }
        )
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = adapter

        loadData()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fabAdd.setOnClickListener {
            showServiceDialog()
        }
    }

    private fun loadData() {
        serviceList.clear()
        val cursor = serviceDao.getAllServices()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name_service"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price_service"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                serviceList.add(MaintenanceService(id, name, price, desc))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    // --- DIALOG THÊM / SỬA DỊCH VỤ ---
    private fun showServiceDialog(service: MaintenanceService? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (service == null) "Thêm Dịch Vụ Mới" else "Cập Nhật Dịch Vụ")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val edtName = EditText(this).apply {
            hint = "Tên dịch vụ (VD: Thay dầu)"
            setText(service?.name)
        }
        val edtPrice = EditText(this).apply {
            hint = "Giá tiền (VNĐ)"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(service?.price?.toInt()?.toString())
        }
        val edtDesc = EditText(this).apply {
            hint = "Mô tả ngắn gọn"
            setText(service?.description)
        }

        layout.addView(edtName)
        layout.addView(edtPrice)
        layout.addView(edtDesc)
        builder.setView(layout)

        builder.setPositiveButton("Lưu") { _, _ ->
            val name = edtName.text.toString().trim()
            val priceStr = edtPrice.text.toString().trim()
            val desc = edtDesc.text.toString().trim()

            if (name.isNotEmpty() && priceStr.isNotEmpty()) {
                val price = priceStr.toDouble()
                if (service == null) {
                    serviceDao.insertService(name, price, desc)
                    Toast.makeText(this, "Đã thêm dịch vụ", Toast.LENGTH_SHORT).show()
                } else {
                    serviceDao.updateService(service.id, name, price, desc)
                    Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show()
                }
                loadData()
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ Tên và Giá!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun confirmDelete(service: MaintenanceService) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa dịch vụ: ${service.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                serviceDao.deleteService(service.id)
                loadData()
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}