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
            put("car_brand", booking.carBrand)
            put("services", booking.services)
            put("booking_date", booking.bookingDate)
            put("booking_time", booking.bookingTime)
            put("promo_code", booking.promoCode)
            put("total_price", booking.totalPrice)
            put("final_price", booking.finalPrice)
            put("note", booking.note)
            put("status", booking.status)
            put("created_at", booking.createdAt)
        }
        return db.insert("bookings", null, values)
    }

    fun getBookingsByUser(userId: Int): List<Booking> {
        val list = mutableListOf<Booking>()
        val cursor = db.rawQuery(
            "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC, booking_time DESC",
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) {
            list.add(Booking(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                carBrand = cursor.getString(cursor.getColumnIndexOrThrow("car_brand")),
                services = cursor.getString(cursor.getColumnIndexOrThrow("services")),
                bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date")),
                bookingTime = cursor.getString(cursor.getColumnIndexOrThrow("booking_time")),
                promoCode = cursor.getString(cursor.getColumnIndexOrThrow("promo_code")),
                totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                finalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("final_price")),
                note = cursor.getString(cursor.getColumnIndexOrThrow("note")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            ))
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
}
