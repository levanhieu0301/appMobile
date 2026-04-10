
package com.example.appcar.database

import android.content.ContentValues
import android.content.Context

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    fun insert(fullName: String, username: String, password: String, role: String) {
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("username", username)
            put("password", password)
            put("role", role)
        }
        db.insert("users", null, values)
    }

    // Kiểm tra email đã có trong DB chưa
    fun isEmailExists(email: String): Boolean {
        val cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Lấy thông tin mật khẩu và role để validate Login
    fun getUserCredentials(email: String): Pair<String, String>? {
        val cursor = db.rawQuery("SELECT password, role FROM users WHERE username = ?", arrayOf(email))
        if (cursor.moveToFirst()) {
            val result = Pair(cursor.getString(0), cursor.getString(1))
            cursor.close()
            return result
        }
        cursor.close()
        return null
    }
    fun getUser(email: String): Triple<String, String, String>? {
        val cursor = db.rawQuery(
            "SELECT full_name, password, role FROM users WHERE username = ?",
            arrayOf(email)
        )

        if (cursor.moveToFirst()) {
            val result = Triple(
                cursor.getString(0), // full_name
                cursor.getString(1), // password
                cursor.getString(2)  // role
            )
            cursor.close()
            return result
        }

        cursor.close()
        return null
    }
    fun getFullNameByEmail(email: String): String? {
        val cursor = db.rawQuery(
            "SELECT full_name FROM users WHERE username = ?",
            arrayOf(email)
        )

        var fullName: String? = null

        if (cursor.moveToFirst()) {
            fullName = cursor.getString(0)
        }

        cursor.close()
        return fullName
    }
}