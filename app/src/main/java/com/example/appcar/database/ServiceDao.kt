package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.appcar.R
import com.example.appcar.client.ServiceItem

class ServiceDao(private val context: Context) {
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
    fun getAllServicesList(): List<ServiceItem> {
        val list = mutableListOf<ServiceItem>()

        val cursor = db.rawQuery(
            "SELECT id_service, name_service, price_service, description, image FROM services",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val price = cursor.getDouble(2)
                val desc = cursor.getString(3)
                val imageName = cursor.getString(4) // lấy tên ảnh từ DB

                // 🔥 convert tên ảnh -> drawable
                val imageResId = context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )

                list.add(
                    ServiceItem(
                        id,
                        if (imageResId != 0) imageResId else android.R.drawable.ic_menu_gallery,
                        name,
                        "${price.toInt()}đ",
                        desc
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }
}