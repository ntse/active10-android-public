package com.phe.betterhealth.components.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.widgets.databinding.BhCarouselDoubleSubitemBinding

class CarouselDoubleItemsAdapter :
    RecyclerView.Adapter<CarouselDoubleItemsAdapter.ViewHolder>() {

    var items = emptyList<List<ArticlePageWrapper>>()

    var isTouchExplorationEnabled = false

    lateinit var onItemClickListener: (ArticlePage) -> Unit

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        BhCarouselDoubleSubitemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val itemFirst = item.getOrNull(0)?.infoPage
        val itemSecond = item.getOrNull(1)?.infoPage

        with(holder.binding) {
            infoPage1 = itemFirst
            infoPage2 = itemSecond
            hideImage = isTouchExplorationEnabled
            executePendingBindings()

            if (itemFirst != null) {
                item1.root.setOnClickListener { onItemClickListener(itemFirst) }
            } else item1.root.setOnClickListener(null)
            if (itemSecond != null) {
                item2.root.setOnClickListener { onItemClickListener(itemSecond) }
            } else item2.root.setOnClickListener(null)
        }
    }

    class ViewHolder(val binding: BhCarouselDoubleSubitemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
