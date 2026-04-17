package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class BookingDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insert(booking: Booking): Long {
        val values = ContentValues().apply {
            put("user_id", booking.userId)
            put("service_type", booking.serviceType)
            put("booking_date", booking.bookingDate)
            put("status", booking.status)
            put("created_at", booking.createdAt)
        }
        return db.insert("bookings", null, values)
    }

    fun getBookingsByUser(userId: Int): List<Booking> {
        val list = mutableListOf<Booking>()
        val cursor = db.rawQuery(
            "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC",
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val serviceType = cursor.getString(cursor.getColumnIndexOrThrow("service_type"))
            val bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            list.add(Booking(id, userId, serviceType, bookingDate, status, createdAt))
        }
        cursor.close()
        return list
    }

    fun updateStatus(bookingId: Int, newStatus: String): Boolean {
        val values = ContentValues().apply { put("status", newStatus) }
        val rows = db.update("bookings", values, "id = ?", arrayOf(bookingId.toString()))
        return rows > 0
    }

    fun deleteBooking(bookingId: Int): Boolean {
        val rows = db.delete("bookings", "id = ?", arrayOf(bookingId.toString()))
        return rows > 0
    }

    fun getBookingById(bookingId: Int): Booking? {
        val cursor = db.rawQuery("SELECT * FROM bookings WHERE id = ?", arrayOf(bookingId.toString()))
        if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            val serviceType = cursor.getString(cursor.getColumnIndexOrThrow("service_type"))
            val bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            cursor.close()
            return Booking(bookingId, userId, serviceType, bookingDate, status, createdAt)
        }
        cursor.close()
        return null
    }
}