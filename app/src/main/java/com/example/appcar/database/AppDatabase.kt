package com.example.appcar.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Tăng version lên 2 để hệ thống nhận diện thay đổi cấu trúc bảng
class AppDatabase(context: Context) : SQLiteOpenHelper(
    context, "app_db", null, 2
) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Bảng Người dùng
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name TEXT," +
                    "username TEXT," +
                    "password TEXT," +
                    "role TEXT)"
        )

        // 2. Bảng Khuyến mãi
        db.execSQL(
            "CREATE TABLE promotions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code TEXT UNIQUE NOT NULL," +
                    "discount_percent INTEGER NOT NULL," +
                    "expiry_date TEXT NOT NULL," +
                    "usage_limit INTEGER NOT NULL," +
                    "used_count INTEGER DEFAULT 0," +
                    "is_active INTEGER DEFAULT 1)"
        )

        // 3. Bổ sung: Bảng Dịch vụ bảo dưỡng
        db.execSQL(
            "CREATE TABLE services (" +
                    "id_service INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name_service TEXT NOT NULL," +
                    "price_service REAL NOT NULL," + // REAL cho kiểu dữ liệu số thực (tiền)
                    "description TEXT)"
        )
        db.execSQL(
            "CREATE TABLE repair_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "customer_name TEXT," +
                    "car_model TEXT," +
                    "repair_date TEXT," +
                    "description TEXT," +
                    "cost TEXT)"
        )
        db.execSQL(
            "CREATE TABLE appointments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," + // Tương ứng với serviceName
                    "date TEXT," +
                    "loc TEXT," +  // Tương ứng với location
                    "status TEXT," +
                    "price INTEGER)"
        )
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS users")
            db.execSQL("DROP TABLE IF EXISTS promotions")
            db.execSQL("DROP TABLE IF EXISTS services")
            db.execSQL("DROP TABLE IF EXISTS repair_history")
            db.execSQL("DROP TABLE IF EXISTS appointments")

            onCreate(db)
        }
    }
}