package com.example.appcar

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
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
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        
        btnBack.setOnClickListener {
            finish() 
        }

        rvHistory = findViewById(R.id.rvRepairHistory)
        dao = RepairHistoryDAO(this)

        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = RepairHistoryAdapter(emptyList()) { history -> 
            confirmDelete(history) 
        }
        rvHistory.adapter = adapter

        loadData()
    }

    private fun loadData() {
        val list = dao.getAll()
        adapter.updateData(list)
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