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
            put("service_id", booking.serviceId)
            put("name", booking.serviceType) // Tên dịch vụ
            put("phone", booking.phone)
            put("date", booking.bookingDate)
            put("time", booking.time)
            put("loc", booking.loc)
            put("note", booking.note)
            put("status", booking.status)
            put("price", booking.price)
            put("created_at", booking.createdAt)
        }
        return db.insert("appointments", null, values)
    }

    fun getBookingsByUser(userId: Int): List<Booking> {
        val list = mutableListOf<Booking>()
        val cursor = db.rawQuery(
            "SELECT * FROM appointments WHERE user_id = ? ORDER BY date DESC, time DESC",
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val serviceId = if (cursor.isNull(cursor.getColumnIndexOrThrow("service_id"))) null else cursor.getInt(cursor.getColumnIndexOrThrow("service_id"))
            val serviceType = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            val bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            val loc = cursor.getString(cursor.getColumnIndexOrThrow("loc"))
            val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            
            list.add(Booking(
                id = id,
                userId = userId,
                serviceId = serviceId,
                name = "", // customer name if needed, but here serviceType is stored in 'name' col
                phone = phone,
                serviceType = serviceType,
                bookingDate = bookingDate,
                time = time,
                loc = loc,
                note = note,
                status = status,
                price = price,
                createdAt = createdAt
            ))
        }
        cursor.close()
        return list
    }

    fun updateStatus(bookingId: Int, newStatus: String): Boolean {
        val values = ContentValues().apply { put("status", newStatus) }
        val rows = db.update("appointments", values, "id = ?", arrayOf(bookingId.toString()))
        return rows > 0
    }

    fun deleteBooking(bookingId: Int): Boolean {
        val rows = db.delete("appointments", "id = ?", arrayOf(bookingId.toString()))
        return rows > 0
    }
}
