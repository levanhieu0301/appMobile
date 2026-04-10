
package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase


    fun insert(fullName: String, username: String, password: String, role: String): Long {
        val values = ContentValues().apply {
            put("full_name", fullName)
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