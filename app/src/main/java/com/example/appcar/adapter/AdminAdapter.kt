package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.R

data class User(
    val id: Int,
    val username: String,
    val role: String
)

class AdminAdapter(
    private var adminList: MutableList<User>,
    private val onDeleteClick: (User) -> Unit,
    private val onEditClick: (User) -> Unit
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtEmail: TextView = itemView.findViewById(R.id.txtAdminEmail)
        val txtRole: TextView = itemView.findViewById(R.id.txtAdminRole)
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

        holder.txtEmail.text = admin.username
        holder.txtRole.text = "Role: ${admin.role}"

        holder.btnDelete.setOnClickListener {
            onDeleteClick(admin)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(admin)
        }
    }

    override fun getItemCount(): Int = adminList.size

    fun updateData(newList: MutableList<User>) {
        adminList = newList
        notifyDataSetChanged()
    }
}