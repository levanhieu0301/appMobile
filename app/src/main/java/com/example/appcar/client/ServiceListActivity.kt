package com.example.appcar.client

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.adapter.ServiceListAdapter
import com.example.appcar.databinding.ActivityServiceListBinding

class ServiceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val services = listOf(
            ServiceItem(
                id = 1,
                imageRes = android.R.drawable.ic_menu_gallery,
                name = "Rửa xe",
                priceText = "50.000đ",
                shortDesc = "Rửa xe sạch sẽ trong 15 phút"
            ),
            ServiceItem(
                id = 2,
                imageRes = android.R.drawable.ic_menu_gallery,
                name = "Thay dầu",
                priceText = "120.000đ",
                shortDesc = "Thay dầu nhanh, kiểm tra mức dầu miễn phí"
            ),
            ServiceItem(
                id = 3,
                imageRes = android.R.drawable.ic_menu_gallery,
                name = "Bảo dưỡng",
                priceText = "250.000đ",
                shortDesc = "Bảo dưỡng định kỳ, kiểm tra tổng quát xe"
            ),
            ServiceItem(
                id = 4,
                imageRes = android.R.drawable.ic_menu_gallery,
                name = "Kiểm tra lốp",
                priceText = "60.000đ",
                shortDesc = "Kiểm tra áp suất, vá lốp (nếu cần)"
            )
        )

        val adapter = ServiceListAdapter(services) { item ->
            Toast.makeText(this, "Đặt ngay: ${item.name}", Toast.LENGTH_SHORT).show()
        }

        binding.rvServiceList.adapter = adapter
        binding.rvServiceList.addItemDecoration(VerticalSpacingDecoration(12.dp))
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    private class VerticalSpacingDecoration(private val spacePx: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val pos = parent.getChildAdapterPosition(view)
            if (pos == RecyclerView.NO_POSITION) return
            if (pos > 0) outRect.top = spacePx
        }
    }
}

