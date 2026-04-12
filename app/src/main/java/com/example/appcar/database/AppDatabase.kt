
package com.example.appcar.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabase(context: Context) : SQLiteOpenHelper(
    context, "app_db", null, 1
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name TEXT," +
                    "username TEXT," +
                    "password TEXT," +
                    "role TEXT)"
        )
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
        db.execSQL("DROP TABLE IF EXISTS users")
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS promotions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code TEXT UNIQUE NOT NULL," +
                    "discount_percent INTEGER NOT NULL," +
                    "expiry_date TEXT NOT NULL," +
                    "usage_limit INTEGER NOT NULL," +
                    "used_count INTEGER DEFAULT 0," +
                    "is_active INTEGER DEFAULT 1)")
        }
        onCreate(db)
    }
}