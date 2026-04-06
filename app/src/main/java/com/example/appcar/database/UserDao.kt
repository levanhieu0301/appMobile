package com.example.appcar.database

import android.content.ContentValues
import android.content.Context

class UserDAO(context: Context) {

    private val db = MyDatabase(context).writableDatabase

    fun insert(username: String, password: String, role: String) {
        val values = ContentValues()
        values.put("username", username)
        values.put("password", password)
        values.put("role", role)

        db.insert("users", null, values)
    }

    fun login(username: String, password: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        val result = cursor.count > 0
        cursor.close()
        return result
    }
}