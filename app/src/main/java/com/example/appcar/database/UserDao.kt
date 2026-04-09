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
import android.database.Cursor

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    fun insert(username: String, password: String, role: String): Long {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("role", role)
        }
        return db.insert("users", null, values)
    }

    fun getAllAdmins(): Cursor {
        return db.rawQuery("SELECT id as _id, username, role FROM users WHERE role = 'admin'", null)
    }

    fun updateAdmin(id: Int, newUsername: String, newRole: String): Int {
        val values = ContentValues().apply {
            put("username", newUsername)
            put("role", newRole)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteUser(id: Int): Int {
        return db.delete("users", "id = ?", arrayOf(id.toString()))
    }

    fun isEmailExists(email: String): Boolean {
        val cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updateAdminWithPass(id: Int, email: String, pass: String, role: String): Int {
        val values = ContentValues().apply {
            put("username", email)
            put("password", pass)
            put("role", role)
        }
        return db.update("users", values, "_id=?", arrayOf(id.toString()))
    }

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

    fun updatePassword(id: Int, newHashPass: String): Int {
        val values = ContentValues().apply {
            put("password", newHashPass)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }
}