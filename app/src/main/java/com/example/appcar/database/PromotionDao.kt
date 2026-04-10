package com.example.appcar.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class PromotionDAO(context: Context) {
    private val dbHelper = AppDatabase(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    // Thêm một mã khuyến mãi mới vào bảng Promotions
    fun insert(promotion: Promotion): Long {
        val values = ContentValues().apply {
            put("code", promotion.code)
            put("discount_percent", promotion.discountPercent)
            put("expiry_date", promotion.expiryDate)
            put("usage_limit", promotion.usageLimit)
            put("used_count", promotion.usedCount)
            put("is_active", if (promotion.isActive) 1 else 0)
        }
        return db.insert("promotions", null, values)
    }
    // Lấy danh sách tất cả các mã khuyến mãi, sắp xếp theo id giảm dần (mới nhất lên đầu)
    fun getAll(): List<Promotion> {
        val list = mutableListOf<Promotion>()
        val cursor = db.query("promotions", null, null, null, null, null, "id DESC")
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
            val discount = cursor.getInt(cursor.getColumnIndexOrThrow("discount_percent"))
            val expiry = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
            val limit = cursor.getInt(cursor.getColumnIndexOrThrow("usage_limit"))
            val used = cursor.getInt(cursor.getColumnIndexOrThrow("used_count"))
            val active = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
            list.add(Promotion(id, code, discount, expiry, limit, used, active))
        }
        cursor.close()
        return list
    }
    // Xóa một mã khuyến mãi dựa trên id
    fun deleteById(id: Int): Int {
        return db.delete("promotions", "id = ?", arrayOf(id.toString()))
    }
    // Tăng số lượt đã sử dụng (used_count) lên 1 cho mã khuyến mãi có code tương ứng
    fun updateUsedCount(code: String) {
        db.execSQL(
            "UPDATE promotions SET used_count = used_count + 1 WHERE code = ?",
            arrayOf(code)
        )
    }

    fun getPromotionByCode(code: String): Promotion? {
        val cursor = db.rawQuery("SELECT * FROM promotions WHERE code = ?", arrayOf(code))
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val discount = cursor.getInt(cursor.getColumnIndexOrThrow("discount_percent"))
            val expiry = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
            val limit = cursor.getInt(cursor.getColumnIndexOrThrow("usage_limit"))
            val used = cursor.getInt(cursor.getColumnIndexOrThrow("used_count"))
            val active = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
            cursor.close()
            return Promotion(id, code, discount, expiry, limit, used, active)
        }
        cursor.close()
        return null
    }
}