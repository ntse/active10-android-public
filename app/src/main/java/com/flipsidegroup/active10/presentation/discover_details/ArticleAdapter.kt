package com.flipsidegroup.active10.presentation.discover_details

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ArticleItemBulletListBinding
import com.flipsidegroup.active10.databinding.ArticleItemBulletListItemBinding
import com.flipsidegroup.active10.databinding.ArticleItemNumberedListBinding
import com.flipsidegroup.active10.databinding.ArticleItemWhiteBulletListBinding
import com.flipsidegroup.active10.utils.toWlpHeader
import com.phe.betterhealth.widgets.common.setHtml
import com.phe.betterhealth.widgets.common.setPaddingBottom
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonCtaBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonMissionBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonShareBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemContentBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemHeadingBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemImageBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemLinkBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemVideoBinding
import com.phe.betterhealth.widgets.databinding.BhArticleListItemArticleBinding
import com.phe.betterhealth.widgets.databinding.BhArticleListItemHeaderBinding
import com.phe.betterhealth.widgets.databinding.BhArticlewlpItemHeaderBinding
import com.phe.betterhealth.widgets.databinding.BhArticlewlpItemNumberedListItemBinding
import com.phe.betterhealth.widgets.databinding.BhArticlewlpItemWhiteBulletListItemBinding

class ArticleAdapter : ListAdapter<ArticlePartAT, RecyclerView.ViewHolder>(differ) {

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (ArticleViewTypeAT.values()[viewType]) {
            ArticleViewTypeAT.HEADER -> HeaderViewHolder(
                BhArticlewlpItemHeaderBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.HEADING -> HeadingViewHolder(
                BhArticleItemHeadingBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.CONTENT -> ContentViewHolder(
                BhArticleItemContentBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.LINK -> LinkViewHolder(
                BhArticleItemLinkBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.CTA -> ButtonCtaViewHolder(
                BhArticleItemButtonCtaBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.SHARE -> ButtonShareViewHolder(
                BhArticleItemButtonShareBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.MISSION -> ButtonMissionViewHolder(
                BhArticleItemButtonMissionBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.BULLET_LIST -> BulletListViewHolder(
                ArticleItemBulletListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.WHITE_BULLET_LIST -> WhiteBulletListViewHolder(
                ArticleItemWhiteBulletListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.NUMBERED_LIST -> NumberedListViewHolder(
                ArticleItemNumberedListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.IMAGE -> ImageViewHolder(
                BhArticleItemImageBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.VIDEO -> VideoViewHolder(
                BhArticleItemVideoBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.RELATED_ARTICLE -> RelatedArticleItemViewHolder(
                BhArticleListItemArticleBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeAT.RELATED_ARTICLE_HEADER -> RelatedArticleHeaderViewHolder(
                BhArticleListItemHeaderBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = getItem(position)

        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder, part as ArticleHeaderAT)

            is HeadingViewHolder -> bindHeadingViewHolder(holder, part as ArticleHeadingAT)
            is ContentViewHolder -> bindContentViewHolder(
                holder,
                part as ArticleContentAT,
                position
            )

            is LinkViewHolder -> bindLinkViewHolder(holder, part as ArticleLinkAT, position)
            is ButtonCtaViewHolder -> bindButtonCtaViewHolder(
                holder = holder,
                item = part as ArticleButtonCtaAT,
                position = position
            )

            is ButtonShareViewHolder -> bindButtonShareViewHolder(
                holder = holder,
                item = part as ArticleButtonShareAT,
                position = position
            )

            is ButtonMissionViewHolder -> bindButtonMissionViewHolder(
                holder = holder,
                item = part as ArticleButtonMissionAT,
                position = position
            )

            is BulletListViewHolder -> bindBulletViewHolder(
                holder = holder,
                item = part as ArticleBulletListAT
            )

            is WhiteBulletListViewHolder -> bindWhiteBulletViewHolder(
                holder = holder,
                item = part as ArticleWhiteBulletListAT
            )

            is NumberedListViewHolder -> bindNumberedViewHolder(
                holder = holder,
                item = part as ArticleNumberedListAT
            )

            is ImageViewHolder -> bindImageViewHolder(
                holder = holder,
                item = part as ArticleImageAT
            )

            is VideoViewHolder -> bindVideoViewHolder(
                holder = holder,
                item = part as ArticleVideoAT
            )

            is RelatedArticleItemViewHolder -> bindRelatedArticleViewHolder(
                holder = holder,
                item = part as ArticleRelatedAT
            )

            is RelatedArticleHeaderViewHolder -> bindRelatedArticleHeaderViewHolder(
                holder = holder,
                item = part as ArticleRelatedHeaderAT
            )
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, item: ArticleHeaderAT) {
        val backgroundColor = holder.binding.root.context.getColor(R.color.colorPrimary)
        val whiteColor = holder.binding.root.context.getColor(R.color.white)
        with(holder.binding) {
            header = item.toWlpHeader(backgroundColor)
            articleItemHeaderContainer.setBackgroundColor(backgroundColor)
            articleItemHeaderContainer.setPaddingBottom(24f)
            articleItemHeaderFrame.setBackgroundColor(whiteColor)
            articleItemHeaderFrame.setPaddingBottom(24f)
            executePendingBindings()
        }
    }

    private fun bindContentViewHolder(
        holder: ContentViewHolder,
        item: ArticleContentAT,
        position: Int
    ) {
        with(holder.binding) {
            content = item.content.body
            isLast = itemCount - 1 == position
            executePendingBindings()
        }
    }

    private fun bindBulletViewHolder(
        holder: BulletListViewHolder,
        item: ArticleBulletListAT
    ) {
        holder.binding.articleItemBulletListContainer.removeAllViews()

        item.list.forEachIndexed { _, itemList ->
            val itemBinding = ArticleItemBulletListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemBulletListContainer,
                false
            )

            itemBinding.bulletListItemContent.setHtml(itemList)
            holder.binding.articleItemBulletListContainer.addView(itemBinding.root)
        }
    }

    private fun bindWhiteBulletViewHolder(
        holder: WhiteBulletListViewHolder,
        item: ArticleWhiteBulletListAT
    ) {
        holder.binding.articleItemBulletListContainer.removeAllViews()

        item.list.forEachIndexed { _, itemList ->
            val itemBinding = BhArticlewlpItemWhiteBulletListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemBulletListContainer,
                false
            )

            itemBinding.whiteBulletListItemContent.setHtml(itemList)
            holder.binding.articleItemBulletListContainer.addView(itemBinding.root)
        }
    }

    private fun bindNumberedViewHolder(
        holder: NumberedListViewHolder,
        item: ArticleNumberedListAT
    ) {
        holder.binding.articleItemNumberedListContainer.removeAllViews()

        item.list.forEachIndexed { index, itemList ->
            val itemBinding = BhArticlewlpItemNumberedListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemNumberedListContainer,
                false
            )

            itemBinding.numberedListItemContent.setHtml(itemList)
            itemBinding.numberedListItemNumber.text = (index + 1).toString()
            itemBinding.numberedListItemNumber.setTextColor(Color.BLACK)
            holder.binding.articleItemNumberedListContainer.addView(itemBinding.root)
        }
    }

    private fun bindLinkViewHolder(holder: LinkViewHolder, item: ArticleLinkAT, position: Int) {
        with(holder.binding) {
            content = item.content.body
            contentDescription = item.contentDescription

            isLast = itemCount - 1 == position
            executePendingBindings()

            if (item.isMentalHealthColors) {
                articleItemLinkButton.backgroundTintList =
                    ColorStateList.valueOf(root.context.getColor(R.color.colorMentalHealth))
            }


            with(articleItemLinkButton) {
                setOnClickListener {
                    item.onClickCallback(item)
                }
            }
        }
    }

    private fun bindHeadingViewHolder(holder: HeadingViewHolder, item: ArticleHeadingAT) {
        with(holder.binding) {
            heading =
                HtmlCompat.fromHtml(item.heading.body!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    .toString()
            executePendingBindings()
        }
    }

    private fun bindButtonCtaViewHolder(
        holder: ButtonCtaViewHolder,
        item: ArticleButtonCtaAT,
        position: Int,
    ) {
        with(holder.binding) {
            title = item.title
            isLast = itemCount - 1 == position
            executePendingBindings()

            if (item.isMentalHealthColors) {
                articleItemCtaButton.backgroundTintList =
                    ColorStateList.valueOf(root.context.getColor(R.color.colorMentalHealth))
            }

            articleItemCtaButton.setOnClickListener { item.onClickCallback() }
        }
    }

    private fun bindButtonMissionViewHolder(
        holder: ButtonMissionViewHolder,
        item: ArticleButtonMissionAT,
        position: Int,
    ) {
        with(holder.binding) {
            isCompleted = item.isCompleted
            isLast = itemCount - 1 == position

            executePendingBindings()

            articleItemCompleteButton.setOnClickListener { item.onClickCallback() }
        }
    }

    private fun bindButtonShareViewHolder(
        holder: ButtonShareViewHolder,
        item: ArticleButtonShareAT,
        position: Int,
    ) {
        with(holder.binding) {
            button = item.toArticlePart()
            isLast = itemCount - 1 == position
            executePendingBindings()

            clickListener = View.OnClickListener { item.onClickCallback() }
        }
    }

    private fun bindRelatedArticleViewHolder(
        holder: RelatedArticleItemViewHolder,
        item: ArticleRelatedAT
    ) {
        with(holder.binding) {
            val infoPage = item.articlePage

            imageUrl = infoPage.imageUrl
            bodyTitle = infoPage.title
            bodyDescription = infoPage.description
            categoryLabel = infoPage.categoryLabel

            executePendingBindings()

            root.setOnClickListener { item.onClickCallback(item) }
        }
    }

    private fun bindRelatedArticleHeaderViewHolder(
        holder: RelatedArticleHeaderViewHolder,
        item: ArticleRelatedHeaderAT
    ) {
        with(holder.binding) {
            title = holder.binding.root.context.getString(item.headerTextRes)
            executePendingBindings()
        }
    }


    private fun bindImageViewHolder(
        holder: ImageViewHolder,
        item: ArticleImageAT
    ) {
        with(holder.binding) {
            imageTitle = item.title
            imageUrl = item.imageUrl
            executePendingBindings()
        }
    }

    private fun bindVideoViewHolder(
        holder: VideoViewHolder,
        item: ArticleVideoAT
    ) {
        with(holder.binding) {
            imageUrl = item.imageUrl

            root.setOnClickListener { item.onClickCallback(item.youtubeUrl) }
            executePendingBindings()
        }
    }

    private class HeaderViewHolder(val binding: BhArticlewlpItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ContentViewHolder(val binding: BhArticleItemContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class LinkViewHolder(val binding: BhArticleItemLinkBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class HeadingViewHolder(val binding: BhArticleItemHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonMissionViewHolder(val binding: BhArticleItemButtonMissionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonShareViewHolder(val binding: BhArticleItemButtonShareBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonCtaViewHolder(val binding: BhArticleItemButtonCtaBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class BulletListViewHolder(val binding: ArticleItemBulletListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class WhiteBulletListViewHolder(val binding: ArticleItemWhiteBulletListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class NumberedListViewHolder(val binding: ArticleItemNumberedListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ImageViewHolder(val binding: BhArticleItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class VideoViewHolder(val binding: BhArticleItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticleItemViewHolder(val binding: BhArticleListItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticleHeaderViewHolder(val binding: BhArticleListItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<ArticlePartAT>() {
            override fun areItemsTheSame(oldItem: ArticlePartAT, newItem: ArticlePartAT): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ArticlePartAT,
                newItem: ArticlePartAT
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
