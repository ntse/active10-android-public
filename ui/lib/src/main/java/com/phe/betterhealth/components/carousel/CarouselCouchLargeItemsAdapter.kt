package com.phe.betterhealth.components.carousel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.widgets.databinding.BhCarouselCouchLargeSubitemBinding

class CarouselCouchLargeItemsAdapter :
    RecyclerAdapter<ArticlePageWrapper, CarouselCouchLargeItemsAdapter.ViewHolder>(differ) {

    var completedMissionIds = emptySet<Long>()

    var isTouchExplorationEnabled = false

    lateinit var onItemClickListener: (ArticlePage) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        BhCarouselCouchLargeSubitemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            imageUrl = item.infoPage.imageUrl
            categoryLabel = item.infoPage.categoryLabel
            bodyTitle = item.infoPage.title
            bodyDescription = item.infoPage.description
            hideImage = isTouchExplorationEnabled
            isCompleted = item.missionId in completedMissionIds
            contentDescription = buildString {
                append(getCarouselSlideContentDescription(item.infoPage, true))
                append(" - ")
                if (item.missionId != null) {
                    append(" - ")
                    append(getMissionContentDescription(isCompleted!!))
                }
                append("element ${position + 1} of ${currentList.size}")
            }
            executePendingBindings()

            root.setOnClickListener { onItemClickListener(item.infoPage) }
        }
    }

    class ViewHolder(val binding: BhCarouselCouchLargeSubitemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<ArticlePageWrapper>() {
            override fun areItemsTheSame(
                oldItem: ArticlePageWrapper, newItem: ArticlePageWrapper
            ) = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ArticlePageWrapper, newItem: ArticlePageWrapper
            ) = oldItem == newItem
        }
    }
}
