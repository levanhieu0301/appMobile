package com.example.appcar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.model.Appointment
import com.google.android.material.button.MaterialButton

/* Code cũ
class AppointmentAdapter(private val list: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServiceName: TextView = view.findViewById(R.id.tvServiceName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvServiceName.text = item.serviceName
        holder.tvDate.text = item.date
        holder.tvPrice.text = String.format("%,d VNĐ", item.price)
        holder.tvStatus.text = item.status

        when (item.status) {
            "Hoàn thành" -> holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            "Đã hủy" -> holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
            else -> holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
        }
    }

    override fun getItemCount(): Int = list.size
}
*/

// Code mới hỗ trợ Duyệt/Hủy cho Admin
class AppointmentAdapter(
    private val list: MutableList<Appointment>,
    private val onConfirmClick: (Appointment) -> Unit,
    private val onCancelClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCarBrand: TextView = view.findViewById(R.id.tvCarBrand)
        val tvServices: TextView = view.findViewById(R.id.tvServices)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnConfirm: MaterialButton = view.findViewById(R.id.btnConfirm)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancel)
        val layoutActions: View = view.findViewById(R.id.layoutActionButtons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvCarBrand.text = "Hãng xe: ${item.carBrand}"
        holder.tvServices.text = "Dịch vụ: ${item.services}"
        holder.tvDate.text = "Ngày: ${item.date}"
        holder.tvTime.text = "Giờ: ${item.time}"
        holder.tvPrice.text = "Tổng: ${String.format("%,.0f", item.finalPrice)}đ"
        holder.tvStatus.text = "Trạng thái: ${item.status}"

        // Cấu hình màu sắc và hiển thị nút dựa trên trạng thái
        when (item.status) {
            "PENDING" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#7C3AED"))
                holder.layoutActions.visibility = View.VISIBLE
            }
            "CONFIRMED" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#10B981"))
                holder.layoutActions.visibility = View.GONE
            }
            "CANCELLED" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#EF4444"))
                holder.layoutActions.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.setTextColor(Color.GRAY)
                holder.layoutActions.visibility = View.GONE
            }
        }

        holder.btnConfirm.setOnClickListener { onConfirmClick(item) }
        holder.btnCancel.setOnClickListener { onCancelClick(item) }
    }

    override fun getItemCount(): Int = list.size
}
