package com.example.appcar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.adapter.PromotionAdapter
import com.example.appcar.database.PromotionDAO

class ManagePromotionActivity : AppCompatActivity() {

    private lateinit var rvPromotions: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var dao: PromotionDAO
    private lateinit var adapter: PromotionAdapter
    private val promotionList = mutableListOf<com.example.appcar.database.Promotion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_promotion)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        rvPromotions = findViewById(R.id.rvPromotions)
        btnAdd = findViewById(R.id.btnAddPromotion)
        dao = PromotionDAO(this)

        rvPromotions.layoutManager = LinearLayoutManager(this)
        adapter = PromotionAdapter(
            promotionList,
            onEditClick = { promotion ->
                // Mở màn hình chỉnh sửa, truyền id của promotion
                val intent = Intent(this, EditPromotionActivity::class.java)
                intent.putExtra("promotion_id", promotion.id)
                startActivity(intent)
            },
            onDeleteClick = { promotion ->
                dao.deleteById(promotion.id)
                loadData()
                Toast.makeText(this, "Đã xoá mã ${promotion.code}", Toast.LENGTH_SHORT).show()
            }
        )
        rvPromotions.adapter = adapter

        btnAdd.setOnClickListener {
            startActivity(Intent(this, CreatePromotionActivity::class.java))
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        promotionList.clear()
        promotionList.addAll(dao.getAll())
        adapter.notifyDataSetChanged()
    }

    // BẮT BUỘC: xử lý khi nhấn nút back
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()  // hoặc finish()
        return true
    }
}