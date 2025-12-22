package com.flipsidegroup.active10.presentation.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ItemFaqItemBinding

class FaqAdapter : ListAdapter<FaqListItem, FaqAdapter.ItemViewHolder>(differ) {

    var onItemClickListener: (FaqListItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemFaqItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        bindItemViewHolder(holder, position)
    }

    private fun bindItemViewHolder(
        holder: ItemViewHolder, position: Int
    ) {
        val item = getItem(position)
        with(holder.binding) {
            text = item.faqItem.title
            longText = item.faqItem.description
            expanded = item.isExpanded
            executePendingBindings()

            contentDescription = if (!item.isExpanded) {
                "${item.faqItem.title}. section collapsed."
            } else {
                "${item.faqItem.title}. section expanded. ${item.faqItem.description}"
            }

            itemIcon.rotation = if (item.isExpanded) 180f else 0f
            itemTitle.typeface = ResourcesCompat.getFont(itemTitle.context,
                if (item.isExpanded) R.font.roboto_bold else R.font.roboto_regular
            )

            root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    class ItemViewHolder(val binding: ItemFaqItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<FaqListItem>() {
            override fun areItemsTheSame(
                oldItem: FaqListItem, newItem: FaqListItem
            ) = oldItem.faqItem.id == newItem.faqItem.id

            override fun areContentsTheSame(
                oldItem: FaqListItem, newItem: FaqListItem
            ) = false
        }
    }
}
