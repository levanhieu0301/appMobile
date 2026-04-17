package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepairHistoryDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    // Lấy tất cả bản ghi
    fun getAll(): List<RepairHistory> {
        val list = mutableListOf<RepairHistory>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM repair_history ORDER BY id DESC", null)
        while (cursor.moveToNext()) {
            val history = RepairHistory(
                id = cursor.getLong(0),
                customerName = cursor.getString(1),
                carModel = cursor.getString(2),
                repairDate = cursor.getString(3),
                description = cursor.getString(4),
                cost = cursor.getString(5)
            )
            list.add(history)
        }
        cursor.close()
        return list
    }

    // Thêm mới
    fun insert(history: RepairHistory): Long {
        val values = ContentValues().apply {
            put("customer_name", history.customerName)
            put("car_model", history.carModel)
            put("repair_date", history.repairDate)
            put("description", history.description)
            put("cost", history.cost)
        }
        return db.insert("repair_history", null, values)
    }

    // Xóa bản ghi
    fun delete(id: Long): Int {
        return db.delete("repair_history", "id = ?", arrayOf(id.toString()))
    }

    fun getTodayRevenue(): Double {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cursor = db.rawQuery(
            "SELECT SUM(cost) FROM repair_history WHERE repair_date LIKE ?",
            arrayOf("$today%")
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }
}