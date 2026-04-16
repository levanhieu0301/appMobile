package com.example.appcar.client

import androidx.annotation.DrawableRes

data class ServiceItem(
    val id: Int,
    @DrawableRes val imageRes: Int,
    val name: String,
    val priceText: String,
    val shortDesc: String
)

