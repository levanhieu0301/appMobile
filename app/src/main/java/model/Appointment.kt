package com.example.appcar.model

/* Code cũ
data class Appointment(
    val id: Int,
    val serviceName: String,
    val date: String,
    val location: String,
    val status: String,
    val price: Long
)
*/

// Code mới đồng bộ với bảng bookings
data class Appointment(
    val id: Int,
    val userId: Int,
    val carBrand: String,
    val services: String,
    val date: String,
    val time: String,
    val status: String,
    val finalPrice: Double,
    val note: String
)