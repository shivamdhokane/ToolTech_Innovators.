package com.example.tooltechinnovators

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemCartBinding

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onActionClick: (CartItem, CartAction) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    enum class CartAction {
        INCREASE, DECREASE, REMOVE
    }

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.binding.productImage.setImageResource(item.productImageResId)
        holder.binding.productName.text = item.productName
        holder.binding.productPrice.text = item.productPrice
        holder.binding.quantityTv.text = item.quantity.toString()

        holder.binding.increaseBtn.setOnClickListener {
            onActionClick(item, CartAction.INCREASE)
        }

        holder.binding.decreaseBtn.setOnClickListener {
            onActionClick(item, CartAction.DECREASE)
        }

        holder.binding.removeBtn.setOnClickListener {
            onActionClick(item, CartAction.REMOVE)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}





