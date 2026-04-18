package com.example.appcar.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.appcar.R
import com.example.appcar.client.ServiceItem

class ServiceDao(private val context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertService(name: String, price: Double, desc: String, duration: Int, image: String): Long {
        val values = ContentValues().apply {
            put("name_service", name)
            put("price_service", price)
            put("description", desc)
            put("duration", duration)
            put("image", image)
            put("is_active", 1)
            put("created_at", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
        }
        return db.insert("services", null, values)
    }

    fun getAllServices(): Cursor {
        return db.rawQuery("SELECT id_service as _id, name_service, price_service, description, duration, image FROM services", null)
    }

    fun updateService(id: Int, name: String, price: Double, desc: String, duration: Int, image: String): Int {
        val values = ContentValues().apply {
            put("name_service", name)
            put("price_service", price)
            put("description", desc)
            put("duration", duration)
            put("image", image)
        }
        return db.update("services", values, "id_service = ?", arrayOf(id.toString()))
    }

    fun deleteService(id: Int): Int {
        return db.delete("services", "id_service=?", arrayOf(id.toString()))
    }

    @SuppressLint("DiscouragedApi")
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
                
                // 🔥 SỬA LỖI Ở ĐÂY: Kiểm tra null trước khi gọi getIdentifier
                val imageName = if (cursor.isNull(4)) "" else cursor.getString(4)

                val imageResId = if (imageName.isNotEmpty()) {
                    context.resources.getIdentifier(
                        imageName,
                        "drawable",
                        context.packageName
                    )
                } else {
                    0
                }

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
