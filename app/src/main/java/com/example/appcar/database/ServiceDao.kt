package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class ServiceDao(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertService(name: String, price: Double, desc: String): Long {
        val v = ContentValues().apply {
            put("name_service", name)
            put("price_service", price)
            put("description", desc)
        }
        return db.insert("services", null, v)
    }

    fun getAllServices(): Cursor {
        return db.rawQuery("SELECT id_service as _id, name_service, price_service, description FROM services", null)
    }

    fun updateService(id: Int, name: String, price: Double, desc: String): Int {
        val v = ContentValues().apply {
            put("name_service", name)
            put("price_service", price)
            put("description", desc)
        }
        return db.update("services", v, "id_service=?", arrayOf(id.toString()))
    }

    fun deleteService(id: Int): Int {
        return db.delete("services", "id_service=?", arrayOf(id.toString()))
    }
}