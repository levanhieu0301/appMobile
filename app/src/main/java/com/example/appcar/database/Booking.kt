package com.example.appcar.database

data class Booking(
    val id: Int = 0,
    val userId: Int,
    val serviceType: String,
    val bookingDate: String, // yyyy-MM-dd
    val status: String,      // PENDING, CONFIRMED, COMPLETED
    val createdAt: String
)