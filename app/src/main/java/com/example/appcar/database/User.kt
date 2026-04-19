package com.example.appcar.database

data class User(
    var id: Int = 0,
    var fullName: String? = null,
    var username: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var address: String? = null,
    var avatar: String? = null,
    var gender: String? = null,
    var dateOfBirth: String? = null,
    var role: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null
)