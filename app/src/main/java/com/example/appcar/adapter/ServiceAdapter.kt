package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R

class ServiceAdapter(
    private val serviceList: List<MaintenanceService>,
    private val onDeleteClick: (MaintenanceService) -> Unit,
    private val onEditClick: (MaintenanceService) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtNameService)
        val txtPrice: TextView = view.findViewById(R.id.txtPriceService)
        val txtDesc: TextView = view.findViewById(R.id.txtDescService)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditService)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteService)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = serviceList[position]
        holder.txtName.text = service.name
        // Định dạng hiển thị tiền tệ VNĐ
        holder.txtPrice.text = String.format("%,.0f VNĐ", service.price)
        holder.txtDesc.text = service.description

        holder.btnEdit.setOnClickListener { onEditClick(service) }
        holder.btnDelete.setOnClickListener { onDeleteClick(service) }
    }

    override fun getItemCount(): Int = serviceList.size
}