package com.example.appcar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.model.Appointment

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