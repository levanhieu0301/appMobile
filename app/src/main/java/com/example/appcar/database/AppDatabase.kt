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
//        db.execSQL(
//            "CREATE TABLE users (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "full_name TEXT," +
//                    "username TEXT," +
//                    "password TEXT," +
//                    "role TEXT)"
//        )
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name TEXT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "phone TEXT," +
                    "email TEXT," +
                    "address TEXT," +
                    "avatar TEXT," +
                    "gender TEXT," +
                    "date_of_birth TEXT," +
                    "role TEXT," +
                    "created_at TEXT," +
                    "updated_at TEXT)"
        )

        // 2. Bảng Khuyến mãi
//        db.execSQL(
//            "CREATE TABLE promotions (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "code TEXT UNIQUE NOT NULL," +
//                    "discount_percent INTEGER NOT NULL," +
//                    "expiry_date TEXT NOT NULL," +
//                    "usage_limit INTEGER NOT NULL," +
//                    "used_count INTEGER DEFAULT 0," +
//                    "is_active INTEGER DEFAULT 1)"
//        )
        db.execSQL(
            "CREATE TABLE promotions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code TEXT UNIQUE NOT NULL," +
                    "title TEXT," +
                    "description TEXT," +
                    "discount_percent INTEGER," +
                    "max_discount REAL," +
                    "min_order_value REAL," +
                    "start_date TEXT," +
                    "expiry_date TEXT," +
                    "usage_limit INTEGER," +
                    "used_count INTEGER DEFAULT 0," +
                    "is_active INTEGER DEFAULT 1," +
                    "created_at TEXT)"
        )

        // 3. Bổ sung: Bảng Dịch vụ bảo dưỡng
//        db.execSQL(
//            "CREATE TABLE services (" +
//                    "id_service INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "name_service TEXT NOT NULL," +
//                    "price_service REAL NOT NULL," + // REAL cho kiểu dữ liệu số thực (tiền)
//                    "description TEXT)"
//        )
        db.execSQL(
            "CREATE TABLE services (" +
                    "id_service INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name_service TEXT NOT NULL," +
                    "price_service REAL NOT NULL," +
                    "description TEXT," +
                    "image TEXT," +
                    "duration INTEGER," +
                    "category TEXT," +
                    "rating REAL DEFAULT 0," +
                    "is_active INTEGER DEFAULT 1," +
                    "created_at TEXT)"
        )
//        db.execSQL(
//            "CREATE TABLE repair_history (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "customer_name TEXT," +
//                    "car_model TEXT," +
//                    "repair_date TEXT," +
//                    "description TEXT," +
//                    "cost TEXT)"
//        )
        db.execSQL(
            "CREATE TABLE repair_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "customer_name TEXT," +
                    "phone TEXT," +
                    "car_model TEXT," +
                    "license_plate TEXT," +
                    "repair_date TEXT," +
                    "description TEXT," +
                    "cost REAL," +
                    "status TEXT," +
                    "garage_name TEXT," +
                    "created_at TEXT)"
        )
//        db.execSQL(
//            "CREATE TABLE appointments (" +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "name TEXT," + // Tương ứng với serviceName
//                    "date TEXT," +
//                    "loc TEXT," +  // Tương ứng với location
//                    "status TEXT," +
//                    "price INTEGER)"
//        )
        db.execSQL(
            "CREATE TABLE appointments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "service_id INTEGER," +
                    "name TEXT," +
                    "phone TEXT," +
                    "date TEXT," +
                    "time TEXT," +
                    "loc TEXT," +
                    "note TEXT," +
                    "status TEXT," +
                    "price REAL," +
                    "created_at TEXT)"
        )
        // 6. Bổ sung: Bảng Đặt lịch (Dùng cho Client đặt lịch mới)
        db.execSQL(
            "CREATE TABLE bookings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "car_brand TEXT," +
                    "services TEXT," +
                    "booking_date TEXT," +
                    "booking_time TEXT," +
                    "promo_code TEXT," +
                    "total_price REAL," +
                    "final_price REAL," +
                    "note TEXT," +
                    "status TEXT DEFAULT 'PENDING'," +
                    "created_at TEXT)"
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