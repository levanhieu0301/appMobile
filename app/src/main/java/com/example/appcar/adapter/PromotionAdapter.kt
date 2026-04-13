package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.database.Promotion

class PromotionAdapter(
    private val list: MutableList<Promotion>,
    private val onEditClick: (Promotion) -> Unit,
    private val onDeleteClick: (Promotion) -> Unit
) : RecyclerView.Adapter<PromotionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvCode.text = item.code
        holder.tvDiscount.text = "${item.discountPercent}%"
        holder.tvExpiry.text = "Hết hạn: ${item.expiryDate}"
        holder.tvUsage.text = "Đã dùng: ${item.usedCount} / ${item.usageLimit}"
        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        val tvDiscount: TextView = itemView.findViewById(R.id.tvDiscount)
        val tvExpiry: TextView = itemView.findViewById(R.id.tvExpiry)
        val tvUsage: TextView = itemView.findViewById(R.id.tvUsage)

        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

}