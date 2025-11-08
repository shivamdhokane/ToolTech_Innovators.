package com.example.tooltechinnovators

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tooltechinnovators.databinding.ItemSavedAddressBinding

class SavedAddressAdapter(
    private val addresses: List<SavedAddress>,
    private val onAddressSelected: (SavedAddress) -> Unit
) : RecyclerView.Adapter<SavedAddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: ItemSavedAddressBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemSavedAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.binding.addressNameTv.text = address.fullName
        holder.binding.addressMobileTv.text = address.mobileNumber
        holder.binding.addressTextTv.text = address.address
        
        // Show default label if this is the default address
        holder.binding.defaultLabelTv.visibility = if (address.isDefault) ViewGroup.VISIBLE else ViewGroup.GONE
        
        holder.binding.selectAddressBtn.setOnClickListener {
            onAddressSelected(address)
        }
    }

    override fun getItemCount(): Int = addresses.size
}

