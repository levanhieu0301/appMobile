package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import java.text.NumberFormat
import java.util.Locale

class ServiceAdapter(
    private val serviceList: List<MaintenanceService>,
    private val onDeleteClick: (MaintenanceService) -> Unit,
    private val onEditClick: (MaintenanceService) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtNameService)
        val txtDuration: TextView = view.findViewById(R.id.txtDurationService)
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
        holder.txtDesc.text = service.description
        holder.txtDuration.text = "Thời gian: ${service.duration} phút"

        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        holder.txtPrice.text = "${formatter.format(service.price)} VNĐ"

        holder.btnEdit.setOnClickListener { onEditClick(service) }
        holder.btnDelete.setOnClickListener { onDeleteClick(service) }
    }

    override fun getItemCount(): Int = serviceList.size
}