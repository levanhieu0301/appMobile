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
import com.google.android.material.textfield.TextInputEditText

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
            onEditClick = { service -> showEditServiceDialog(service) }
        )
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = adapter

        loadData()

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fabAdd.setOnClickListener {
            showAddServiceDialog()
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
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))

                serviceList.add(MaintenanceService(id, name, price, desc, duration, image))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun showAddServiceDialog() {
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setTitle("Thêm Dịch Vụ Mới")

        val view = layoutInflater.inflate(R.layout.dialog_add_service, null)
        builder.setView(view)

        val edtName = view.findViewById<TextInputEditText>(R.id.edtServiceName)
        val edtPrice = view.findViewById<TextInputEditText>(R.id.edtServicePrice)
        val edtDuration = view.findViewById<TextInputEditText>(R.id.edtServiceDuration)
        val edtImage = view.findViewById<TextInputEditText>(R.id.edtServiceImage)
        val edtDesc = view.findViewById<TextInputEditText>(R.id.edtServiceDescription)

        builder.setPositiveButton("Thêm") { dialog, _ ->
            val name = edtName.text.toString().trim()
            val price = edtPrice.text.toString().toDoubleOrNull() ?: 0.0
            val duration = edtDuration.text.toString().toIntOrNull() ?: 0
            val image = edtImage.text.toString().trim()
            val desc = edtDesc.text.toString().trim()

            if (name.isNotEmpty() && price > 0) {
                // Truyền đủ 5 tham số vào DAO
                val result = serviceDao.insertService(name, price, desc, duration, image)
                if (result > 0) {
                    loadData()
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun showEditServiceDialog(service: MaintenanceService) {
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setTitle("Chỉnh Sửa Dịch Vụ")

        val view = layoutInflater.inflate(R.layout.dialog_add_service, null)
        builder.setView(view)

        val edtName = view.findViewById<TextInputEditText>(R.id.edtServiceName)
        val edtPrice = view.findViewById<TextInputEditText>(R.id.edtServicePrice)
        val edtDuration = view.findViewById<TextInputEditText>(R.id.edtServiceDuration)
        val edtImage = view.findViewById<TextInputEditText>(R.id.edtServiceImage)
        val edtDesc = view.findViewById<TextInputEditText>(R.id.edtServiceDescription)

        edtName.setText(service.name)
        edtPrice.setText(service.price.toString())
        edtDuration.setText(service.duration.toString())
        edtImage.setText(service.image)
        edtDesc.setText(service.description)

        builder.setPositiveButton("Cập nhật") { dialog, _ ->
            val name = edtName.text.toString().trim()
            val price = edtPrice.text.toString().toDoubleOrNull() ?: 0.0
            val duration = edtDuration.text.toString().toIntOrNull() ?: 0
            val image = edtImage.text.toString().trim()
            val desc = edtDesc.text.toString().trim()

            if (name.isNotEmpty() && price > 0) {
                val result = serviceDao.updateService(service.id, name, price, desc, duration, image)

                if (result > 0) {
                    loadData()
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Không có thay đổi nào được lưu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập tên và giá hợp lệ!", Toast.LENGTH_SHORT).show()
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