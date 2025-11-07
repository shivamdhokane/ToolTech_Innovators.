package com.example.tooltechinnovators

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemProductBinding

class ProductAdapter(
    private val products: MutableList<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.productImage.setImageResource(product.imageResId)
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = product.price
        holder.binding.buyNowBtn.setOnClickListener { onClick(product) }
    }

    override fun getItemCount(): Int = products.size
}
