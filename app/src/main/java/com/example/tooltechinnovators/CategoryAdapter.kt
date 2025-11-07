package com.example.tooltechinnovators

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemCategoryTabBinding

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var selectedCategory: String = categories.firstOrNull() ?: ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CategoryViewHolder(val binding: ItemCategoryTabBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val isSelected = category == selectedCategory

        holder.binding.categoryTabText.text = category

        // Update appearance based on selection
        if (isSelected) {
            holder.binding.categoryTabText.setBackgroundResource(R.drawable.category_tab_selected_bg)
            holder.binding.categoryTabText.setTextColor(0xFFFFFFFF.toInt())
            holder.binding.root.cardElevation = 6f
        } else {
            holder.binding.categoryTabText.setBackgroundResource(R.drawable.category_tab_bg)
            holder.binding.categoryTabText.setTextColor(0xFF1E3A8A.toInt())
            holder.binding.root.cardElevation = 2f
        }

        holder.itemView.setOnClickListener {
            selectedCategory = category
            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}

