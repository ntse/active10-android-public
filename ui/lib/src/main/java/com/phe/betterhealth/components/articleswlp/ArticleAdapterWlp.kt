package com.phe.betterhealth.components.articleswlp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.common.setHeight
import com.phe.betterhealth.widgets.common.setHtml
import com.phe.betterhealth.widgets.common.setTint
import com.phe.betterhealth.widgets.databinding.*
import com.phe.betterhealth.widgets.utils.getCompatColor
import com.phe.betterhealth.widgets.utils.getThemeAttrColor
import com.phe.betterhealth.widgets.utils.textColor

class ArticleAdapterWlp : ListAdapter<ArticlePartWlp, RecyclerView.ViewHolder>(differ) {

    var isMentalHealth: Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // applies for Bullets/Numbers
    var isSameColorAsText: Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (ArticleViewTypeWlp.values()[viewType]) {
            ArticleViewTypeWlp.HEADER -> HeaderViewHolder(
                BhArticlewlpItemHeaderBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.HEADING -> HeadingViewHolder(
                BhArticlewlpItemHeadingBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.CONTENT -> ContentViewHolder(
                BhArticlewlpItemContentBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.LINK -> LinkViewHolder(
                BhArticlewlpItemLinkBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.CTA -> ButtonCtaViewHolder(
                BhArticlewlpItemButtonCtaBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.SHARE -> ButtonShareViewHolder(
                BhArticlewlpItemButtonShareBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.MISSION -> ButtonMissionViewHolder(
                BhArticlewlpItemButtonMissionBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.BULLET_LIST -> BulletListViewHolder(
                BhArticlewlpItemBulletListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.WHITE_BULLET_LIST -> WhiteBulletListViewHolder(
                BhArticlewlpItemWhiteBulletListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.NUMBERED_LIST -> NumberedListViewHolder(
                BhArticlewlpItemNumberedListBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.RELATED_ARTICLES_HEADING -> RelatedArticlesHeadingViewHolder(
                BhArticlewlpRelatedHeadingBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.ARTICLE -> RelatedArticleViewHolder(
                BhArticlewlpItemRelatedArticleBinding.inflate(inflater, parent, false)
            )

            ArticleViewTypeWlp.SPACER -> SpacerViewHolder(
                BhArticlewlpItemSpacerBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = getItem(position)

        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder, part as ArticleHeaderWlp)
            is HeadingViewHolder -> bindHeadingViewHolder(holder, part as ArticleHeadingWlp)
            is ContentViewHolder -> bindContentViewHolder(
                holder = holder,
                item = part as ArticleContentWlp,
                position = position
            )

            is LinkViewHolder -> bindLinkViewHolder(holder, part as ArticleLinkWlp, position)
            is ButtonCtaViewHolder -> bindButtonCtaViewHolder(
                holder = holder,
                item = part as ArticleButtonCtaWlp,
                position = position
            )

            is ButtonShareViewHolder -> bindButtonShareViewHolder(
                holder = holder,
                item = part as ArticleButtonShareWlp,
                position = position
            )

            is ButtonMissionViewHolder -> bindButtonMissionViewHolder(
                holder = holder,
                item = part as ArticleButtonMissionWlp,
                position = position
            )

            is BulletListViewHolder -> bindBulletListViewHolder(
                holder = holder,
                item = part as ArticleBulletListWlp,
            )

            is WhiteBulletListViewHolder -> bindWhiteBulletListViewHolder(
                holder = holder,
                item = part as ArticleWhiteBulletListWlp
            )

            is NumberedListViewHolder -> bindNumberedListViewHolder(
                holder = holder,
                item = part as ArticleNumberedListWlp
            )

            is RelatedArticlesHeadingViewHolder -> bindRelatedArticlesHeadingViewHolder(
                holder = holder,
                item = part as ArticleRelatedHeaderWlp
            )

            is RelatedArticleViewHolder -> bindRelatedArticleViewHolder(
                holder = holder,
                item = part as ArticleRelatedWlp
            )

            is SpacerViewHolder -> bindSpacerViewHolder(
                holder = holder,
                item = part as ArticleSpacerWlp
            )
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, item: ArticleHeaderWlp) {
        val colorPrimary = holder.binding.root.context.getThemeAttrColor(R.attr.colorPrimary)
        val customColor = item.backgroundColor?.let {
            holder.binding.root.context.getCompatColor(it)
        }

        with(holder.binding) {
            header = item
            executePendingBindings()

            articleItemHeaderContainer.setBackgroundColor(customColor ?: colorPrimary)
            articleItemHeaderFrame.setBackgroundColor(checkBackgroundColor(root, isMentalHealth))
            articleItemHeaderTitle.textColor = when {
                isMentalHealth -> R.color.bhTextPrimary
                else -> R.color.bhWhite
            }
            articleItemHeaderDescription.textColor = when {
                isMentalHealth -> R.color.bhTextSecondary
                else -> R.color.bhWhite
            }
        }
    }

    private fun bindContentViewHolder(
        holder: ContentViewHolder,
        item: ArticleContentWlp,
        position: Int
    ) {
        with(holder.binding) {
            content = item.content.body
            isLast = itemCount - 1 == position
            executePendingBindings()

            articleItemContentTextView.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
        }
    }

    private fun bindBulletListViewHolder(
        holder: BulletListViewHolder,
        item: ArticleBulletListWlp,
    ) {
        with(holder.binding) {
            articleItemBulletListContainer.removeAllViews()
            articleItemBulletListFrame.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
        }
        item.list.forEach {
            val itemBinding = BhArticlewlpItemBulletListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemBulletListContainer,
                false
            )

            setContentDescriptionForBulletList(
                holder.binding.articleItemBulletListContainer,
                itemBinding.bulletListItemContent,
                item.list.size,
                it
            )
            if (isSameColorAsText) {
                itemBinding.bulletListItemIcon.setTint(
                    holder.binding.root.context.getCompatColor(
                        android.R.color.black
                    )
                )
            }
            itemBinding.bulletListItemContent.setHtml(it)
            holder.binding.articleItemBulletListContainer.addView(itemBinding.root)
        }
    }

    private fun bindWhiteBulletListViewHolder(
        holder: WhiteBulletListViewHolder,
        item: ArticleWhiteBulletListWlp,
    ) {
        with(holder.binding) {
            articleItemWhiteBulletListContainer.removeAllViews()
            articleItemWhiteBulletListFrame.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
        }

        item.list.forEach {
            val itemBinding = BhArticlewlpItemWhiteBulletListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemWhiteBulletListContainer,
                false
            )

            setContentDescriptionForBulletList(
                holder.binding.articleItemWhiteBulletListContainer,
                itemBinding.whiteBulletListItemContent,
                item.list.size,
                it
            )
            itemBinding.whiteBulletListItemContent.setHtml(it)
            holder.binding.articleItemWhiteBulletListContainer.addView(itemBinding.root)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindNumberedListViewHolder(
        holder: NumberedListViewHolder,
        item: ArticleNumberedListWlp,
    ) {
        with(holder.binding) {
            articleItemNumberedListContainer.removeAllViews()
            articleItemNumberedListFrame.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
        }

        item.list.forEachIndexed { index, itemList ->
            val itemBinding = BhArticlewlpItemNumberedListItemBinding.inflate(
                LayoutInflater.from(holder.binding.root.context),
                holder.binding.articleItemNumberedListContainer,
                false
            )

            if (isSameColorAsText) {
                itemBinding.numberedListItemNumber.setTextColor(
                    holder.binding.root.context.getCompatColor(
                        android.R.color.black
                    )
                )
            }

            itemBinding.numberedListItemContent.setHtml(itemList)
            itemBinding.numberedListItemNumber.text = (index + 1).toString()
            itemBinding.contentDescription =
                "${HtmlCompat.fromHtml("${index + 1}<br>$itemList", HtmlCompat.FROM_HTML_MODE_COMPACT)}"
            holder.binding.articleItemNumberedListContainer.addView(itemBinding.root)
        }
    }

    @Suppress("DEPRECATION")
    private fun bindLinkViewHolder(holder: LinkViewHolder, item: ArticleLinkWlp, position: Int) {
        with(holder.binding) {
            content = item.content.body
            contentDescription = item.contentDescription

            isLast = itemCount - 1 == position
            executePendingBindings()

            articleItemLinkLayout.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
            item.backgroundColor?.let {
                articleItemLinkButton.setBackgroundColor(
                    holder.binding.root.context.getCompatColor(it)
                )
            }
            with(articleItemLinkButton) {
                setOnClickListener {
                    item.onClickCallback(item)
                }
            }
        }
    }

    private fun bindHeadingViewHolder(holder: HeadingViewHolder, item: ArticleHeadingWlp) {
        val content = item.heading.body!!

        with(holder.binding) {
            heading = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
            executePendingBindings()

            articleItemHeadingText.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
            articleItemHeadingText.textSize = when {
                "<h1>" in content -> 31f
                "<h2>" in content -> 27f
                else -> 23f
            }
        }
    }

    private fun bindButtonCtaViewHolder(
        holder: ButtonCtaViewHolder,
        item: ArticleButtonCtaWlp,
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
        item: ArticleButtonMissionWlp,
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
        item: ArticleButtonShareWlp,
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
        item: ArticleRelatedHeaderWlp
    ) {
        with(holder.binding) {
            heading = item.title
            body = item.description
            executePendingBindings()

            articleRelatedHeadingText.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
            articleRelatedContentText.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )
        }
    }

    private fun bindRelatedArticleViewHolder(
        holder: RelatedArticleViewHolder,
        item: ArticleRelatedWlp
    ) {
        with(holder.binding) {
            ViewCompat.setAccessibilityDelegate(articleItemRelatedTitle, accessibilityDelegate)
            ViewCompat.setAccessibilityDelegate(
                articleItemRelatedDescription,
                accessibilityDelegate
            )

            articlePage = item.articlePage
            executePendingBindings()

            articleItemRelatedImage.load(item.articlePage.imageUrl)
            articleItemRelatedIcon.setImageResource(R.drawable.bh_chevron_right)
            articleItemRelatedIcon.setTint(holder.binding.root.context.getCompatColor(item.indicatorColor))
            articleItemRelatedLayout.setBackgroundColor(
                checkBackgroundColor(root, isMentalHealth)
            )

            root.setOnClickListener { item.onClickCallback(item) }
        }
    }

    private fun bindSpacerViewHolder(
        holder: SpacerViewHolder,
        item: ArticleSpacerWlp
    ) {
        with(holder.binding) {
            container.setHeight(item.heightInDp)
            executePendingBindings()
        }
    }

    private fun checkBackgroundColor(root: View, isMentalHealth: Boolean) =
        root.context.getCompatColor(
            when {
                isMentalHealth -> R.color.bhWhite
                else -> R.color.bhArticleWlpBackground
            }
        )

    private fun setContentDescriptionForBulletList(
        layout: LinearLayout,
        textView: TextView,
        listSize: Int,
        content: String
    ) {
        textView.contentDescription =
            if (layout.isEmpty()) "List start. List item 1 of ${listSize}. $content"
            else "List item ${layout.size + 1} of ${listSize}. $content"
    }

    private class HeaderViewHolder(val binding: BhArticlewlpItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ContentViewHolder(val binding: BhArticlewlpItemContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class LinkViewHolder(val binding: BhArticlewlpItemLinkBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class HeadingViewHolder(val binding: BhArticlewlpItemHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonMissionViewHolder(val binding: BhArticlewlpItemButtonMissionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonShareViewHolder(val binding: BhArticlewlpItemButtonShareBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ButtonCtaViewHolder(val binding: BhArticlewlpItemButtonCtaBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class BulletListViewHolder(val binding: BhArticlewlpItemBulletListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class WhiteBulletListViewHolder(val binding: BhArticlewlpItemWhiteBulletListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class NumberedListViewHolder(val binding: BhArticlewlpItemNumberedListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticlesHeadingViewHolder(val binding: BhArticlewlpRelatedHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class RelatedArticleViewHolder(val binding: BhArticlewlpItemRelatedArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SpacerViewHolder(val binding: BhArticlewlpItemSpacerBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<ArticlePartWlp>() {
            override fun areItemsTheSame(
                oldItem: ArticlePartWlp,
                newItem: ArticlePartWlp
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ArticlePartWlp,
                newItem: ArticlePartWlp
            ): Boolean {
                return oldItem == newItem
            }
        }

        val accessibilityDelegate = object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val text = info.text.toString()

                if (text.contains("5k")) {
                    info.text = text.replace("5k", "5 K")
                }
                if (text.contains("5K")) {
                    info.text = text.replace("5K", "5 K")
                }
            }
        }
    }
}
