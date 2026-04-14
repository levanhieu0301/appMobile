package com.example.appcar.database

data class RepairHistory(
    val id: Long,
    val customerName: String,
    val carModel: String,
    val repairDate: String,
    val description: String,
    val cost: String
)