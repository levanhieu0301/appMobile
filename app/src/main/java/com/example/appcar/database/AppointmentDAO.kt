package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class AppointmentDAO(context: Context) {
    // Gọi AppDatabase để mở kết nối
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    // Hàm lấy toàn bộ lịch sử đặt lịch
    fun getAllAppointments(): Cursor {
        return db.rawQuery("SELECT id, name, date, loc, status, price FROM appointments ORDER BY date DESC", null)
    }

    // Hàm thêm một lịch đặt mới (Dùng khi người dùng bấm đặt lịch)
    fun insertAppointment(name: String, date: String, loc: String, status: String, price: Long): Long {
        val values = ContentValues().apply {
            put("name", name)
            put("date", date)
            put("loc", loc)
            put("status", status)
            put("price", price)
        }
        return db.insert("appointments", null, values)
    }

    // Hàm lọc theo trạng thái (Hoàn thành / Đã hủy)
    fun getAppointmentsByStatus(status: String): Cursor {
        return db.rawQuery(
            "SELECT id, name, date, loc, status, price FROM appointments WHERE status = ? ORDER BY date DESC",
            arrayOf(status)
        )
    }

    // Hàm xóa lịch đặt (nếu cần)
    fun deleteAppointment(id: Int): Int {
        return db.delete("appointments", "id = ?", arrayOf(id.toString()))
    }
}