
package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase
    // data mới
    // INSERT FULL (đăng ký)
    fun insertFull(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        address: String,
        password: String,
        role: String
    ): Long {
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("username", username)
            put("email", email)
            put("phone", phone)
            put("address", address)
            put("password", password)
            put("role", role)
        }
        return db.insert("users", null, values)
    }

    // Check email tồn tại
    fun isEmailExists(email: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE email = ?",
            arrayOf(email)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Check username tồn tại
    fun isUsernameExists(username: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE username = ?",
            arrayOf(username)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    fun getUserInfoByEmail(email: String): Pair<String, String>? {
        val cursor = db.rawQuery(
            "SELECT full_name, address FROM users WHERE email = ?",
            arrayOf(email)
        )

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val address = cursor.getString(1)
            cursor.close()
            Pair(name, address)
        } else {
            cursor.close()
            null
        }
    }

    //LOGIN (email + password)
    fun login(email: String, password: String): Int? {
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
    }

    // Lấy full user theo id
    fun getUserById(id: Int): Cursor {
        return db.rawQuery(
            "SELECT * FROM users WHERE id = ?",
            arrayOf(id.toString())
        )
    }

    // Lấy tên + địa chỉ (hiển thị Home)
    fun getUserInfo(id: Int): Pair<String, String>? {
        val cursor = db.rawQuery(
            "SELECT full_name, address FROM users WHERE id = ?",
            arrayOf(id.toString())
        )

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val address = cursor.getString(1)
            cursor.close()
            Pair(name, address)
        } else {
            cursor.close()
            null
        }
    }

    // Admin list
    fun getAllAdmins(): Cursor {
        return db.rawQuery(
            "SELECT id as _id, username, full_name, role FROM users WHERE role = 'admin'",
            null
        )
    }

    // Update user
    fun updateUser(id: Int, username: String, role: String): Int {
        val values = ContentValues().apply {
            put("username", username)
            put("role", role)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    // Update password
    fun updatePassword(id: Int, password: String): Int {
        val values = ContentValues().apply {
            put("password", password)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }

    // Delete user
    fun deleteUser(id: Int): Int {
        return db.delete("users", "id = ?", arrayOf(id.toString()))
    }


    // End data mới

    fun insert(fullName: String, username: String, password: String, role: String): Long {
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("username", username)
            put("password", password)
            put("role", role)
        }
        return db.insert("users", null, values)
    }


    fun updateAdmin(id: Int, newUsername: String, newRole: String): Int {
        val values = ContentValues().apply {
            put("username", newUsername)
            put("role", newRole)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }


    fun updateAdminWithPass(id: Int, email: String, pass: String, role: String): Int {
        val values = ContentValues().apply {
            put("username", email)
            put("password", pass)
            put("role", role)
        }
        return db.update("users", values, "_id=?", arrayOf(id.toString()))
    }

//    fun getUserCredentials(email: String): Pair<String, String>? {
//        val cursor = db.rawQuery("SELECT password, role FROM users WHERE username = ?", arrayOf(email))
//        if (cursor.moveToFirst()) {
//            val result = Pair(cursor.getString(0), cursor.getString(1))
//            cursor.close()
//            return result
//        }
//        cursor.close()
//        return null
//    }
fun getUserCredentials(email: String): Pair<String, String>? {
    val cursor = db.rawQuery(
        "SELECT password, role FROM users WHERE email = ?",
        arrayOf(email)
    )

    return if (cursor.moveToFirst()) {
        val password = cursor.getString(0)
        val role = cursor.getString(1)
        cursor.close()
        Pair(password, role)
    } else {
        cursor.close()
        null
    }
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

    fun getAllUsers(): Cursor {
        // Lấy đầy đủ các trường: id (as _id), full_name, username để hiển thị lên danh sách
        return db.rawQuery(
            "SELECT id as _id, full_name, username, role FROM users WHERE role = 'user'",
            null
        )
    }

    // hàm này để lấy ID người dùng từ Email
    fun getUserIdByEmail(email: String): Int? {
        val cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", arrayOf(email))
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
    }

    // hàm này để lấy Số điện thoại người dùng từ Email
    fun getUserPhoneByEmail(email: String): String {
        val cursor = db.rawQuery("SELECT phone FROM users WHERE email = ?", arrayOf(email))
        return if (cursor.moveToFirst()) {
            val phone = cursor.getString(0) ?: ""
            cursor.close()
            phone
        } else {
            cursor.close()
            ""
        }
    }
    fun getFullNameByEmail(email: String): String? {
        val cursor = db.rawQuery(
            "SELECT full_name FROM users WHERE email = ?",
            arrayOf(email)
        )

        var fullName: String? = null

        if (cursor.moveToFirst()) {
            fullName = cursor.getString(0)
        }

        cursor.close()
        return fullName
    }

    fun updateUser(id: Int, fullName: String, username: String, role: String): Int {
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("username", username)
            put("role", role)
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }
}