package com.example.tooltechinnovators

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemAdminOrderBinding

class AdminOrdersAdapter(
    private val orders: List<Order>,
    private val onStatusChange: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrdersAdapter.AdminOrderViewHolder>() {

    inner class AdminOrderViewHolder(val binding: ItemAdminOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.orderIdTv.text = "Order #${order.orderId.take(8)}"
        holder.binding.orderDateTv.text = "Date: ${order.orderDate}"
        holder.binding.orderTotalTv.text = order.totalAmount
        holder.binding.orderItemsTv.text = "Items: ${order.items.size}"
        holder.binding.orderStatusTv.text = order.status
        
        // Set status color
        when (order.status.lowercase()) {
            "pending" -> {
                holder.binding.orderStatusTv.setTextColor(0xFFFF6B00.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFFFE5D4.toInt())
            }
            "accepted" -> {
                holder.binding.orderStatusTv.setTextColor(0xFF2563EB.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFDBEAFE.toInt())
            }
            "rejected" -> {
                holder.binding.orderStatusTv.setTextColor(0xFFDC2626.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFFEE2E2.toInt())
            }
            "delivering" -> {
                holder.binding.orderStatusTv.setTextColor(0xFF7C3AED.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFEDE9FE.toInt())
            }
            "delivered" -> {
                holder.binding.orderStatusTv.setTextColor(0xFF16A34A.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFD1FAE5.toInt())
            }
            else -> {
                holder.binding.orderStatusTv.setTextColor(0xFF6B7280.toInt())
                holder.binding.orderStatusTv.setBackgroundColor(0xFFF3F4F6.toInt())
            }
        }
        
        // Show/hide buttons based on current status
        when (order.status.lowercase()) {
            "pending" -> {
                holder.binding.acceptBtn.visibility = ViewGroup.VISIBLE
                holder.binding.rejectBtn.visibility = ViewGroup.VISIBLE
                holder.binding.deliveredBtn.visibility = ViewGroup.GONE
            }
            "accepted", "delivering" -> {
                holder.binding.acceptBtn.visibility = ViewGroup.GONE
                holder.binding.rejectBtn.visibility = ViewGroup.GONE
                holder.binding.deliveredBtn.visibility = ViewGroup.VISIBLE
            }
            "rejected", "delivered" -> {
                holder.binding.acceptBtn.visibility = ViewGroup.GONE
                holder.binding.rejectBtn.visibility = ViewGroup.GONE
                holder.binding.deliveredBtn.visibility = ViewGroup.GONE
            }
        }
        
        // Button click listeners
        holder.binding.acceptBtn.setOnClickListener {
            onStatusChange(order, "Delivering")
        }
        
        holder.binding.rejectBtn.setOnClickListener {
            onStatusChange(order, "Rejected")
        }
        
        holder.binding.deliveredBtn.setOnClickListener {
            onStatusChange(order, "Delivered")
        }
    }

    override fun getItemCount(): Int = orders.size
}

