package com.example.appcar.model

data class Appointment(
    val id: Int,
    val serviceName: String,
    val date: String,
    val location: String,
    val status: String,
    val price: Long
)