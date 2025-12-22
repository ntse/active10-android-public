package com.phe.betterhealth.components.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.parseAsHtml
import androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO
import androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.databinding.BhArticleItemBulletListBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemBulletListItemBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonCtaBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonMentalHealthCtaBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonMissionBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemButtonShareBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemContentBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemHeaderBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemHeaderWithBackgroundBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemHeadingBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemImageBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemLinkBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemNumberedListBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemNumberedListItemBinding
import com.phe.betterhealth.widgets.databinding.BhArticleItemVideoBinding
import com.phe.betterhealth.widgets.databinding.BhArticleMentalHealthItemLinkBinding
import com.phe.betterhealth.widgets.databinding.BhArticleRelatedHeadingBinding
import com.phe.betterhealth.widgets.databinding.BhArticleRelatedItemHorizontalBinding
import com.phe.betterhealth.widgets.utils.getCompatColor
import com.phe.betterhealth.widgets.utils.getThemeAttrColor
import com.phe.betterhealth.widgets.utils.textColor

class ArticleAdapter : ListAdapter<ArticlePart, RecyclerView.ViewHolder>(differ) {

    var onLinkClickListener: (url: String?) -> Unit = {}

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (ArticleViewType.values()[viewType]) {
            ArticleViewType.HEADER -> HeaderViewHolder(
                BhArticleItemHeaderBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.HEADER_WITH_BACKGROUND -> HeaderWithBackgroundViewHolder(
                BhArticleItemHeaderWithBackgroundBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.HEADING -> HeadingViewHolder(
                BhArticleItemHeadingBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.CONTENT -> ContentViewHolder(
                BhArticleItemContentBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.LINK -> LinkViewHolder(
                BhArticleItemLinkBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.CTA -> ButtonCtaViewHolder(
                BhArticleItemButtonCtaBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.MENTAL_HEALTH_CTA -> ButtonMentalHealthCtaViewHolder(
                BhArticleItemButtonMentalHealthCtaBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.SHARE -> ButtonShareViewHolder(
                BhArticleItemButtonShareBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.MISSION -> ButtonMissionViewHolder(
                BhArticleItemButtonMissionBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.BULLET_LIST -> BulletListViewHolder(
                BhArticleItemBulletListBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.NUMBERED_LIST -> NumberedListViewHolder(
                BhArticleItemNumberedListBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.IMAGE -> ImageViewHolder(
                BhArticleItemImageBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.VIDEO -> VideoViewHolder(
                BhArticleItemVideoBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.RELATED_ARTICLES_HEADING -> RelatedArticlesHeadingViewHolder(
                BhArticleRelatedHeadingBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.RELATED_ARTICLE -> RelatedArticleViewHolder(
                BhArticleRelatedItemHorizontalBinding.inflate(inflater, parent, false)
            )

            ArticleViewType.MENTAL_LINK -> LinkMentalHealthViewHolder(
                BhArticleMentalHealthItemLinkBinding.inflate(inflater, parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = getItem(position)

        when (holder) {
            is ImageViewHolder -> bindImageViewHolder(holder, part as ArticleImage)
            is VideoViewHolder -> bindVideoViewHolder(holder, part as ArticleVideo)
            is HeaderViewHolder -> bindHeaderViewHolder(holder, part as ArticleHeader)
            is HeaderWithBackgroundViewHolder -> bindHeaderWithBackgroundViewHolder(
                holder,
                part as ArticleHeaderWithBackground
            )

            is HeadingViewHolder -> bindHeadingViewHolder(holder, part as ArticleHeading)
            is ContentViewHolder -> bindContentViewHolder(holder, part as ArticleContent, position)
            is LinkViewHolder -> bindLinkViewHolder(holder, part as ArticleLink, position)
            is LinkMentalHealthViewHolder -> bindMentalHealthLinkViewHolder(
                holder,
                part as ArticleMentalHealthLink,
                position
            )

            is ButtonCtaViewHolder -> bindButtonCtaViewHolder(
                holder = holder,
                item = part as ArticleButtonCta,
                position = position
            )

            is ButtonMentalHealthCtaViewHolder -> bindButtonMentalHealthCtaViewHolder(
                holder = holder,
                item = part as ArticleButtonMentalHealthCta,
                position = position
            )

            is ButtonShareViewHolder -> bindButtonShareViewHolder(
                holder = holder,
                item = part as ArticleButtonShare,
                position = position
            )

            is ButtonMissionViewHolder -> bindButtonMissionViewHolder(
                holder = holder,
                item = part as ArticleButtonMission,
                position = position
            )

            is BulletListViewHolder -> bindBulletViewHolder(
                holder = holder,
                item = part as ArticleBulletList
            )

            is NumberedListViewHolder -> bindNumberedViewHolder(
                holder = holder,
                item = part as ArticleNumberedList
            )

            is RelatedArticlesHeadingViewHolder -> bindRelatedArticlesHeadingViewHolder(
                holder = holder,
                item = part as ArticleRelatedHeader
            )

            is RelatedArticleViewHolder -> bindRelatedArticleViewHolder(
                holder = holder,
                item = part as ArticleRelatedItem
            )
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, item: ArticleHeader) {
        with(holder.binding) {
            header = item
            executePendingBindings()
        }
    }

    private fun bindHeaderWithBackgroundViewHolder(
        holder: HeaderWithBackgroundViewHolder,
        item: ArticleHeaderWithBackground
    ) {
        with(holder.binding) {
            val colorPrimary = holder.binding.root.context.getThemeAttrColor(R.attr.colorPrimary)
            val customColor = item.backgroundColor?.let {
                holder.binding.root.context.getCompatColor(it)
            }
            header = item
            articleItemHeaderContainer.setBackgroundColor(customColor ?: colorPrimary)
            executePendingBindings()
        }
    }

    private fun bindContentViewHolder(
        holder: ContentViewHolder,
        item: ArticleContent,
        position: Int
    ) {
        with(holder.binding) {
            content = item.content.body?.replace("\r\n", "<br/>")?.replace("\n", "<br/>")
            isLast = itemCount - 1 == position
            executePendingBindings()

            val contentText = content?.parseAsHtml()?.toString().orEmpty()
            root.importantForAccessibility =
                if (content?.parseAsHtml()?.toString().isNullOrBlank()) {
                    IMPORTANT_FOR_ACCESSIBILITY_NO
                } else {
                    when {
                        contentText.startsWith("- (i)") -> {
                            root.contentDescription = contentText.replace("- (i)", "1")
                        }

                        contentText.startsWith("- ") -> {
                            root.contentDescription = contentText.replace("- ", "")
                        }

                        else -> root.contentDescription = contentText
                    }

                    IMPORTANT_FOR_ACCESSIBILITY_YES
                }
        }
    }

    private fun bindBulletViewHolder(
        holder: BulletListViewHolder,
        item: ArticleBulletList
    ) {
        holder.binding.articleItemBulletListContainer.removeAllViews()
        holder.binding.listTitle = item.title

        item.list.forEachIndexed { _, itemList ->
            val itemBinding = BhArticleItemBulletListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemBulletListContainer,
                false
            )

            itemBinding.bulletListItemContent.text = itemList
            holder.binding.articleItemBulletListContainer.addView(itemBinding.root)
        }
    }

    private fun bindImageViewHolder(
        holder: ImageViewHolder,
        item: ArticleImage
    ) {
        with(holder.binding) {
            imageTitle = item.title
            imageUrl = item.imageUrl
            executePendingBindings()
        }
    }

    private fun bindVideoViewHolder(
        holder: VideoViewHolder,
        item: ArticleVideo
    ) {
        with(holder.binding) {
            imageUrl = item.imageUrl

            root.setOnClickListener { item.onClickCallback(item.youtubeUrl) }
            executePendingBindings()
        }
    }


    private fun bindNumberedViewHolder(
        holder: NumberedListViewHolder,
        item: ArticleNumberedList
    ) {
        holder.binding.articleItemNumberedListContainer.removeAllViews()
        holder.binding.listTitle = item.title

        item.list.forEachIndexed { index, itemList ->
            val itemBinding = BhArticleItemNumberedListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemNumberedListContainer,
                false
            )

            itemBinding.numberedListItemContent.text = itemList
            itemBinding.numberedListItemNumber.text = (index + 1).toString()
            holder.binding.articleItemNumberedListContainer.addView(itemBinding.root)
        }
    }

    private fun bindLinkViewHolder(holder: LinkViewHolder, item: ArticleLink, position: Int) {
        with(holder.binding) {
            content = item.content.body
            contentDescription = item.contentDescription

            isLast = itemCount - 1 == position
            executePendingBindings()

            with(articleItemLinkButton) {
                setOnClickListener {
                    onLinkClickListener(item.url)
                    item.onClickCallback(item)
                }
            }
        }
    }

    private fun bindMentalHealthLinkViewHolder(
        holder: LinkMentalHealthViewHolder,
        item: ArticleMentalHealthLink,
        position: Int
    ) {
        with(holder.binding) {
            content = item.content.body
            contentDescription = item.contentDescription

            isLast = itemCount - 1 == position
            executePendingBindings()

            with(articleItemLinkButton) {
                setOnClickListener {
                    onLinkClickListener(item.url)
                    item.onClickCallback(item)
                }
            }
        }
    }

    private fun bindHeadingViewHolder(holder: HeadingViewHolder, item: ArticleHeading) {
        with(holder.binding) {
            heading = item.heading.body?.parseAsHtml(FROM_HTML_MODE_COMPACT)?.toString()
            executePendingBindings()

            root.importantForAccessibility = if (heading.isNullOrBlank()) {
                IMPORTANT_FOR_ACCESSIBILITY_NO
            } else IMPORTANT_FOR_ACCESSIBILITY_YES
        }
    }

    private fun bindButtonCtaViewHolder(
        holder: ButtonCtaViewHolder,
        item: ArticleButtonCta,
        position: Int,
    ) {
        with(holder.binding) {
            title = item.title
            isLast = itemCount - 1 == position
            executePendingBindings()

            articleItemCtaButton.setOnClickListener { item.onClickCallback() }
        }
    }

    private fun bindButtonMentalHealthCtaViewHolder(
        holder: ButtonMentalHealthCtaViewHolder,
        item: ArticleButtonMentalHealthCta,
        position: Int,
    ) {
        with(holder.binding) {
            title = item.title
            isLast = itemCount - 1 == position

            executePendingBindings()

            articleItemCtaButton.setOnClickListener { item.onClickCallback() }
        }
    }

    private fun bindButtonMissionViewHolder(
        holder: ButtonMissionViewHolder,
        item: ArticleButtonMission,
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
        item: ArticleButtonShare,
        position: Int,
    ) {
        with(holder.binding) {
            button = item
            isLast = itemCount - 1 == position
            executePendingBindings()

            clickListener = View.OnClickListener { item.onClickCallback() }
        }
    }

    private fun bindRelatedArticlesHeadingViewHolder(
        holder: RelatedArticlesHeadingViewHolder,
        item: ArticleRelatedHeader
    ) {
        with(holder.binding) {
            heading = item.title
            body = item.description
            executePendingBindings()
        }
    }

    private fun bindRelatedArticleViewHolder(
        holder: RelatedArticleViewHolder,
        item: ArticleRelatedItem
    ) {
        with(holder.binding) {
            categoryLabel = item.article.categoryLabel
            bodyTitle = item.article.title
            imageUrl = item.article.imageUrl

            root.setOnClickListener {
                item.onClickCallback.invoke(item.article)
            }

            executePendingBindings()
        }
    }

    private class HeaderViewHolder(val binding: BhArticleItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class HeaderWithBackgroundViewHolder(val binding: BhArticleItemHeaderWithBackgroundBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ContentViewHolder(val binding: BhArticleItemContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class LinkViewHolder(val binding: BhArticleItemLinkBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class LinkMentalHealthViewHolder(val binding: BhArticleMentalHealthItemLinkBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class HeadingViewHolder(val binding: BhArticleItemHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonMissionViewHolder(val binding: BhArticleItemButtonMissionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonShareViewHolder(val binding: BhArticleItemButtonShareBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonCtaViewHolder(val binding: BhArticleItemButtonCtaBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonMentalHealthCtaViewHolder(val binding: BhArticleItemButtonMentalHealthCtaBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class BulletListViewHolder(val binding: BhArticleItemBulletListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class NumberedListViewHolder(val binding: BhArticleItemNumberedListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ImageViewHolder(val binding: BhArticleItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class VideoViewHolder(val binding: BhArticleItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticlesHeadingViewHolder(val binding: BhArticleRelatedHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticleViewHolder(val binding: BhArticleRelatedItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<ArticlePart>() {
            override fun areItemsTheSame(oldItem: ArticlePart, newItem: ArticlePart): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ArticlePart, newItem: ArticlePart): Boolean {
                return oldItem == newItem
            }
        }
    }
}
