package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.database.RepairHistory

class RepairHistoryAdapter(
    private var list: List<RepairHistory>,
    private val onEdit: (RepairHistory) -> Unit,
    private val onDelete: (RepairHistory) -> Unit
) : RecyclerView.Adapter<RepairHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repair_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvCustomerName.text = "Khách: ${item.customerName}"
        holder.tvCarModel.text = "Xe: ${item.carModel}"
        holder.tvRepairDate.text = "Ngày: ${item.repairDate}"
        holder.tvDescription.text = "Mô tả: ${item.description}"
        holder.tvCost.text = "Chi phí: ${item.cost}"

        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RepairHistory>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvCarModel: TextView = itemView.findViewById(R.id.tvCarModel)
        val tvRepairDate: TextView = itemView.findViewById(R.id.tvRepairDate)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}