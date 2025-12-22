package com.flipsidegroup.active10.presentation.walksneardetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ItemWalkNearAttributeBinding

class WalkNearAttributesAdapter :
    ListAdapter<String, WalkNearAttributesAdapter.ViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWalkNearAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemWalkNearAttributeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attribute: String) {
            val resId = when (attribute) {
                "history" -> R.drawable.ic_history
                "botanics" -> R.drawable.ic_botanics
                "wildlife" -> R.drawable.ic_wildlife
                "cost" -> R.drawable.ic_cost
                "water" -> R.drawable.ic_water
                "public-transport" -> R.drawable.ic_public_transport
                "gradients" -> R.drawable.ic_gradients
                "views" -> R.drawable.ic_views
                "wheelchair" -> R.drawable.ic_wheelchair
                "refreshments" -> R.drawable.ic_refreshments
                "toilets" -> R.drawable.ic_toilets
                "picnic" -> R.drawable.ic_picnic
                "parking" -> R.drawable.ic_parking
                "children" -> R.drawable.ic_children
                "dogs" -> R.drawable.ic_dogs
                else -> null
            }
            resId?.let { binding.attribute.setImageResource(it) }
            binding.attribute.contentDescription = attribute.replace("-", " ")
        }
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

}