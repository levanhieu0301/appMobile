//package com.example.appcar.database
//
//import android.content.ContentValues
//import android.content.Context
//
//class UserDAO(context: Context) {
//
//    private val db = AppDatabase(context).writableDatabase
//
//    fun insert(username: String, password: String, role: String) {
//        val values = ContentValues()
//        values.put("username", username)
//        values.put("password", password)
//        values.put("role", role)
//
//        db.insert("users", null, values)
//    }
//
//    fun login(username: String, password: String): Boolean {
//        val cursor = db.rawQuery(
//            "SELECT * FROM users WHERE username=? AND password=?",
//            arrayOf(username, password)
//        )
//
//        val result = cursor.count > 0
//        cursor.close()
//        return result
//    }
//    fun loginAndGetRole(username: String, password: String): String? {
//        val cursor = db.rawQuery(
//            "SELECT role FROM users WHERE username=? AND password=?",
//            arrayOf(username, password)
//        )
//
//        var role: String? = null
//
//        if (cursor.moveToFirst()) {
//            role = cursor.getString(0)
//        }
//
//        cursor.close()
//        return role
//    }
//}
package com.example.appcar.database

import android.content.ContentValues
import android.content.Context

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    fun insert(username: String, password: String, role: String) {
        val values = ContentValues().apply {
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
}