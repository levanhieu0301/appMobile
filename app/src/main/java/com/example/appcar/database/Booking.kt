package com.example.appcar.database

data class Booking(
    val id: Int = 0,
    val userId: Int,
    val serviceId: Int? = null,
    val name: String,
    val phone: String,
    val serviceType: String,
    val bookingDate: String, // yyyy-MM-dd
    val time: String,
    val loc: String,
    val note: String,
    val status: String,      // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val price: Double = 0.0,
    val createdAt: String
)