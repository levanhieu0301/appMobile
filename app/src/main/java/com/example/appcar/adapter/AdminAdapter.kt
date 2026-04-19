package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R
import com.example.appcar.database.User

class AdminAdapter(
    private var adminList: MutableList<User>,
    private val onDeleteClick: (User) -> Unit,
    private val onEditClick: (User) -> Unit
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFullName: TextView = itemView.findViewById(R.id.txtAdminFullName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtAdminEmail)
        val txtPhone: TextView = itemView.findViewById(R.id.txtAdminPhone)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAdminAddress)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditAdmin)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = adminList[position]

        holder.txtFullName.text = admin.fullName
        holder.txtEmail.text = admin.username
        holder.txtPhone.text = "SĐT: ${admin.phone}"
        holder.txtAddress.text = "Địa chỉ: ${admin.address}"

        holder.btnDelete.setOnClickListener {
            onDeleteClick(admin)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(admin)
        }
    }

    override fun getItemCount(): Int = adminList.size

    fun updateData(newList: List<User>) {
        adminList = newList.toMutableList()
        notifyDataSetChanged()
    }
}