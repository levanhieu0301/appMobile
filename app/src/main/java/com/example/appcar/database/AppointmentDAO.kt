package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class AppointmentDAO(context: Context) {
    // Gọi AppDatabase để mở kết nối
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    /* Code cũ lấy từ bảng appointments
    fun getAllAppointments(): Cursor {
        return db.rawQuery("SELECT id, name, date, loc, status, price FROM appointments ORDER BY date DESC", null)
    }
    */

    // Code mới lấy dữ liệu từ bảng bookings (nơi user đặt lịch)
    fun getAllAppointmentsFromBookings(): Cursor {
        return db.rawQuery("SELECT id, user_id, car_brand, services, booking_date, booking_time, status, final_price, note FROM bookings ORDER BY booking_date DESC, booking_time DESC", null)
    }

    // Hàm cập nhật trạng thái lịch hẹn dành cho Admin (Duyệt/Hủy)
    fun updateBookingStatus(id: Int, newStatus: String): Int {
        val values = ContentValues().apply {
            put("status", newStatus)
        }
        return db.update("bookings", values, "id = ?", arrayOf(id.toString()))
    }

    /* Các hàm cũ không còn phù hợp với bảng mới
    fun insertAppointment(name: String, date: String, loc: String, status: String, price: Long): Long { ... }
    fun getAppointmentsByStatus(status: String): Cursor { ... }
    fun deleteAppointment(id: Int): Int { ... }
    */
}