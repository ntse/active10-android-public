package com.phe.betterhealth.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phe.betterhealth.demo.databinding.ItemDemoBinding

class DemoAdapter : RecyclerView.Adapter<DemoAdapter.ItemViewHolder>() {

    var items = listOf<Pair<String, Int>>()

    var onItemClickListener: (Int) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (name, id) = items[position]
        with(holder.binding) {
            title.text = name
            root.setOnClickListener {
                onItemClickListener(id)
            }
        }
    }

    class ItemViewHolder(val binding: ItemDemoBinding) : RecyclerView.ViewHolder(binding.root)
}
