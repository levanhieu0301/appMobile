package com.example.appcar.database

data class Booking(
    val id: Int = 0,
    val userId: Int,
    val carBrand: String,
    val services: String,
    val bookingDate: String,
    val bookingTime: String,
    val promoCode: String,
    val totalPrice: Double,
    val finalPrice: Double,
    val note: String,
    val status: String = "PENDING",
    val createdAt: String
)