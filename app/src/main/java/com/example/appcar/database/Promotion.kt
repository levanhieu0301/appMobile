package com.example.appcar.database

data class Promotion(
    val id: Int = 0,
    val code: String,
    val discountPercent: Int,
    val expiryDate: String,
    val usageLimit: Int,
    val usedCount: Int = 0,
    val isActive: Boolean = true
)