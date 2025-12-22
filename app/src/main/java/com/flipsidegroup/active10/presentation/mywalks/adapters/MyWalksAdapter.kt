package com.flipsidegroup.active10.presentation.mywalks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ItemFuncInfoActionBinding
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.phe.betterhealth.widgets.common.setHtml

class MyWalksAdapter : ListAdapter<ScreenContent, RecyclerView.ViewHolder>(differ) {

    lateinit var onItemClickListener: (actionSlug: String?) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FuncInfoActionItemViewHolder(
            ItemFuncInfoActionBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FuncInfoActionItemViewHolder -> bindFuncInfoActionItemViewHolder(holder, getItem(position))
        }
    }

    private fun bindFuncInfoActionItemViewHolder(holder: FuncInfoActionItemViewHolder, screenContent: ScreenContent) {
        with(holder.binding) {
            title.text = screenContent.title
            description.setHtml(screenContent.description)
            button.text = screenContent.actionTitle
            image.loadFromUrl(screenContent.firstImageUrl)

            button.setOnClickListenerWithDebounce { onItemClickListener(screenContent.actionSlug) }
        }
    }

    private class FuncInfoActionItemViewHolder(val binding: ItemFuncInfoActionBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<ScreenContent>() {
            override fun areItemsTheSame(oldItem: ScreenContent, newItem: ScreenContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ScreenContent, newItem: ScreenContent) =
                oldItem.slug == newItem.slug
                        && oldItem.title == newItem.title
                        && oldItem.description == newItem.description
        }
    }
}