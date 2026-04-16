package com.example.appcar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appcar.client.ServiceItem
import com.example.appcar.databinding.ItemServiceClientBinding

class ServiceListAdapter(
    private val items: List<ServiceItem>,
    private val onBookNowClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<ServiceListAdapter.VH>() {

    class VH(val binding: ItemServiceClientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemServiceClientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.binding) {
            imgService.setImageResource(item.imageRes)
            txtName.text = item.name
            txtPrice.text = item.priceText
            txtDesc.text = item.shortDesc

            btnBookNow.setOnClickListener { onBookNowClick(item) }
        }
    }

    override fun getItemCount(): Int = items.size
}