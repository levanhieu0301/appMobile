
package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db = dbHelper.writableDatabase

    // Lấy đối tượng User theo id (trả về User, không phải Cursor)
    fun getUserByIdAsObject(id: Int): User? {
        val cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(id.toString()))
        return cursor.use {
            if (it.moveToFirst()) {
                User(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    fullName = it.getString(it.getColumnIndexOrThrow("full_name")),
                    username = it.getString(it.getColumnIndexOrThrow("username")),
                    password = it.getString(it.getColumnIndexOrThrow("password")),
                    phone = it.getString(it.getColumnIndexOrThrow("phone")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    address = it.getString(it.getColumnIndexOrThrow("address")),
                    avatar = it.getString(it.getColumnIndexOrThrow("avatar")),
                    gender = it.getString(it.getColumnIndexOrThrow("gender")),
                    dateOfBirth = it.getString(it.getColumnIndexOrThrow("date_of_birth")),
                    role = it.getString(it.getColumnIndexOrThrow("role")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                    updatedAt = it.getString(it.getColumnIndexOrThrow("updated_at"))
                )
            } else null
        }
    }

    // Cập nhật toàn bộ thông tin user (trừ id, password nếu không đổi)
    fun updateUserInfo(user: User): Int {
        val values = ContentValues().apply {
            put("full_name", user.fullName)
            put("username", user.username)
            put("email", user.email)
            put("phone", user.phone)
            put("address", user.address)
            put("avatar", user.avatar)
            put("gender", user.gender)
            put("date_of_birth", user.dateOfBirth)
            put("role", user.role)
            put("updated_at", user.updatedAt)
        }
        return db.update("users", values, "id = ?", arrayOf(user.id.toString()))
    }

    // Cập nhật mật khẩu
    fun updatePassword(userId: Int, newPassword: String): Int {
        val values = ContentValues().apply {
            put("password", newPassword)
        }
        return db.update("users", values, "id = ?", arrayOf(userId.toString()))
    }

    // Cập nhật thông tin cơ bản (dùng cho Profile)
    fun updateUserBasicInfo(
        userId: Int,
        username: String,
        email: String,
        phone: String,
        address: String
    ): Int {
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("phone", phone)
            put("address", address)
        }
        return db.update("users", values, "id = ?", arrayOf(userId.toString()))
    }

    // Thêm vào class UserDAO
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

//    // Update password
//    fun updatePassword(id: Int, password: String): Int {
//        val values = ContentValues().apply {
//            put("password", password)
//        }
//        return db.update("users", values, "id = ?", arrayOf(id.toString()))
//    }

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