package com.example.tooltechinnovators

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemCategoryHeaderBinding
import com.example.tooltechinnovators.databinding.ItemProductBinding

class CategorizedProductAdapter(
    private val products: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PRODUCT = 1
    }

    private val items = mutableListOf<Any>()

    init {
        // Group products by category
        val grouped = products.groupBy { it.category }
        grouped.forEach { (category, categoryProducts) ->
            items.add(category) // Add category header
            items.addAll(categoryProducts) // Add products in this category
        }
    }

    inner class HeaderViewHolder(val binding: ItemCategoryHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> TYPE_HEADER
            is Product -> TYPE_PRODUCT
            else -> TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> {
                (holder as HeaderViewHolder).binding.categoryName.text = item
            }
            is Product -> {
                val productHolder = holder as ProductViewHolder
                productHolder.binding.productImage.setImageResource(item.imageResId)
                productHolder.binding.productName.text = item.name
                productHolder.binding.productPrice.text = item.price
                productHolder.binding.buyNowBtn.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

