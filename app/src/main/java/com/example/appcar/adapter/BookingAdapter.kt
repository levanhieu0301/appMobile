package com.example.appcar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.database.Booking
import com.google.android.material.button.MaterialButton
import java.util.*

class BookingAdapter(
    private val list: MutableList<Booking>,
    private val onCancelClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        
        holder.tvServices.text = "Dịch vụ: ${item.services}"
        holder.tvCarBrand.text = "Hãng xe: ${item.carBrand}"
        holder.tvBookingDate.text = "Ngày: ${item.bookingDate}"
        holder.tvBookingTime.text = "Giờ: ${item.bookingTime}"
        holder.tvFinalPrice.text = "Thành tiền: ${String.format("%,.0f", item.finalPrice)}đ"
        holder.tvStatus.text = "Trạng thái: ${item.status}"

        // Đổi màu trạng thái để dễ phân biệt
        when (item.status) {
            "PENDING" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#7C3AED")) // Tím - Chờ
                holder.btnCancel.visibility = View.VISIBLE
            }
            "CONFIRMED" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#10B981")) // Xanh lá - Đã xác nhận
                holder.btnCancel.visibility = View.GONE 
            }
            "COMPLETED" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#3B82F6")) // Xanh dương - Hoàn thành
                holder.btnCancel.visibility = View.GONE
            }
            "CANCELLED" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#EF4444")) // Đỏ - Đã hủy
                holder.btnCancel.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.setTextColor(Color.GRAY)
                holder.btnCancel.visibility = View.GONE
            }
        }

        holder.btnCancel.setOnClickListener { onCancelClick(item) }
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServices: TextView = itemView.findViewById(R.id.tvServices)
        val tvCarBrand: TextView = itemView.findViewById(R.id.tvCarBrand)
        val tvBookingDate: TextView = itemView.findViewById(R.id.tvBookingDate)
        val tvBookingTime: TextView = itemView.findViewById(R.id.tvBookingTime)
        val tvFinalPrice: TextView = itemView.findViewById(R.id.tvFinalPrice)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnCancel: MaterialButton = itemView.findViewById(R.id.btnCancelBooking)
    }
}
