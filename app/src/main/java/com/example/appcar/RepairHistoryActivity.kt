package com.example.appcar

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.RepairHistoryAdapter
import com.example.appcar.database.RepairHistoryDAO
import com.example.appcar.database.RepairHistory

class RepairHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var dao: RepairHistoryDAO
    private lateinit var adapter: RepairHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair_history)

        rvHistory = findViewById(R.id.rvRepairHistory)
        dao = RepairHistoryDAO(this)

        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = RepairHistoryAdapter(emptyList(),
            { history -> showEditDialog(history) },
            { history -> confirmDelete(history) }
        )
        rvHistory.adapter = adapter

        loadData()
    }

    private fun loadData() {
        val list = dao.getAll()
        adapter.updateData(list)
    }

    // Chỉ còn dialog SỬA, không có dialog THÊM
    private fun showEditDialog(history: RepairHistory) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_repair, null)
        val etCustomer = dialogView.findViewById<EditText>(R.id.etCustomerName)
        val etCar = dialogView.findViewById<EditText>(R.id.etCarModel)
        val etDate = dialogView.findViewById<EditText>(R.id.etRepairDate)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDescription)
        val etCost = dialogView.findViewById<EditText>(R.id.etCost)

        etCustomer.setText(history.customerName)
        etCar.setText(history.carModel)
        etDate.setText(history.repairDate)
        etDesc.setText(history.description)
        etCost.setText(history.cost)

        AlertDialog.Builder(this)
            .setTitle("Sửa thông tin sửa xe")
            .setView(dialogView)
            .setPositiveButton("Cập nhật") { _, _ ->
                val customer = etCustomer.text.toString().trim()
                val car = etCar.text.toString().trim()
                val date = etDate.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                val cost = etCost.text.toString().trim()

                if (customer.isEmpty() || car.isEmpty() || date.isEmpty()) {
                    Toast.makeText(this, "Tên khách, xe và ngày không được để trống", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val updated = history.copy(
                    customerName = customer,
                    carModel = car,
                    repairDate = date,
                    description = desc,
                    cost = cost
                )
                dao.update(updated)
                loadData()
                Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun confirmDelete(history: RepairHistory) {
        AlertDialog.Builder(this)
            .setTitle("Xóa lịch sử")
            .setMessage("Bạn có chắc muốn xóa bản ghi của ${history.customerName}?")
            .setPositiveButton("Xóa") { _, _ ->
                dao.delete(history.id)
                loadData()
                Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}