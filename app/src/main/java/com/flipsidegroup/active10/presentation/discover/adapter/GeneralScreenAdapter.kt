package com.flipsidegroup.active10.presentation.discover.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.components.carousel.CarouselDoubleItemsAdapter
import com.phe.betterhealth.components.carousel.CarouselLargeItemsAdapter
import com.phe.betterhealth.components.carousel.CarouselSmallItemsAdapter
import com.phe.betterhealth.widgets.carousel.BHCarouselIndicator
import com.phe.betterhealth.widgets.carousel.BHCarouselOffsetPageTransformer
import com.phe.betterhealth.widgets.databinding.*
import com.phe.betterhealth.widgets.utils.dpToPx

class GeneralScreenAdapter : ListAdapter<GeneralScreen, RecyclerView.ViewHolder>(differ) {

    private val carouselScrollStates = hashMapOf<Int, BHCarouselIndicator.SavedState?>()

    var onArticleClickListener: (ArticlePage) -> Unit = {}

    var navigateToLinkedCategoryClickListener: (ScreenContent) -> Unit = {}

    override fun getItemViewType(position: Int) =
        getItem(position).viewType?.ordinal ?: GeneralViewType.UNKNOWN.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (GeneralViewType.entries[viewType]) {
            GeneralViewType.ARTICLE_LIST -> ArticleListHeaderViewHolder(
                BhArticleListItemHeaderBinding.inflate(inflater, parent, false),
            )
            GeneralViewType.ARTICLE_LIST_ITEM -> ArticleItemViewHolder(
                BhArticleListItemArticleBinding.inflate(inflater, parent, false)
            )
            GeneralViewType.ARTICLE_LIST_SMALL_ITEMS -> ArticleListHeaderViewHolder(
                BhArticleListItemHeaderBinding.inflate(inflater, parent, false),
            )
            GeneralViewType.ARTICLE_LIST_SMALL_ITEM_ELEMENT-> ArticleItemHorizontalViewHolder(
                BhArticleListItemHorizontalBinding.inflate(inflater, parent, false)
            )
            GeneralViewType.CAROUSEL_SMALL_ITEMS -> CarouselSmallItemsViewHolder(
                BhCarouselSmallBinding.inflate(inflater, parent, false),
            )
            GeneralViewType.CAROUSEL_LARGE_ITEMS -> CarouselLargeItemsViewHolder(
                BhCarouselLargeBinding.inflate(inflater, parent, false),
            )
            GeneralViewType.CAROUSEL_DOUBLE_ITEMS -> CarouselDoubleItemsViewHolder(
                BhCarouselDoubleBinding.inflate(inflater, parent, false),
            )
            GeneralViewType.HERO_ITEM -> HeroItemViewHolder(
                BhDiscoverHeroBinding.inflate(inflater, parent, false),
            )
            else -> throw IllegalStateException("Unknown view type: ${GeneralViewType.entries[viewType]}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CarouselLargeItemsViewHolder -> bindCarouselLargeItems(holder, position)
            is CarouselSmallItemsViewHolder -> bindCarouselSmallItems(holder, position)
            is CarouselDoubleItemsViewHolder -> bindCarouselDoubleItems(holder, position)
            is HeroItemViewHolder -> bindHeroItemViewHolder(holder, getItem(position))
            is ArticleListHeaderViewHolder -> {
                bindArticlesHeaderViewHolder(holder, getItem(position))
            }
            is ArticleItemHorizontalViewHolder ->
                bindArticleItemHorizontalViewHolder(holder, getItem(position)
            )
            is ArticleItemViewHolder -> bindArticleItemViewHolder(holder, getItem(position))
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        carouselScrollStates[holder.bindingAdapterPosition] = when (holder) {
            is CarouselSmallItemsViewHolder -> holder.binding.carouselIndicator
            is CarouselDoubleItemsViewHolder -> holder.binding.carouselIndicator
            is CarouselLargeItemsViewHolder -> holder.binding.carouselIndicator
            else -> null
        }?.currentState
    }

    private fun bindCarouselLargeItems(holder: CarouselLargeItemsViewHolder, position: Int) {
        val screen = getItem(position) as GeneralScreenItem
        val limit = screen.infoPages.size
        val items = if (limit > 0) screen.infoPages.take(limit) else screen.infoPages
        with(holder.adapter) {
            onItemClickListener = onArticleClickListener
            submitList(items)
        }
        with(holder.binding) {
            bodyTitle = screen.item.title
            bodyDescription = screen.item.description
            //buttonTitle = screen.item.firstButtonTitle

            fullWidth = true
            isCompact = false
            executePendingBindings()

            generalItemCarouselLargeContainer.footerButtonView.setOnClickListener {
                navigateToLinkedCategoryClickListener(screen.item)
            }
        }

        holder.binding.viewPager.initializeWithAdapter(holder.adapter)
        with(holder.binding.carouselIndicator) {
            addAfterPageSwitchListener { carouselScrollStates[position] = it }
            setupWithViewPager2(holder.binding.viewPager, carouselScrollStates[position])
        }
    }

    private fun bindCarouselSmallItems(holder: CarouselSmallItemsViewHolder, position: Int) {
        val item = getItem(position) as GeneralScreenItem
        val limit = item.infoPages.size
        val items = if (limit > 0) item.infoPages.take(limit) else item.infoPages
        with(holder.adapter) {
            // completedMissionIds = item.completedMissions
            onItemClickListener = onArticleClickListener
            submitList(items)
        }

        with(holder.binding) {
            bodyTitle = item.item.title
            bodyDescription = item.item.description
            //buttonTitle = item.item.firstButtonTitle

            fullWidth = true
            executePendingBindings()

            generalItemCarouselSmallContainer.footerButtonView.setOnClickListener {
                when (item.item.slug) {
                    else -> navigateToLinkedCategoryClickListener(item.item)
                }
            }
        }

        holder.binding.recyclerView.adapter = holder.adapter
        with(holder.binding.carouselIndicator) {
            addAfterPageSwitchListener { carouselScrollStates[position] = it }
            setupWithRecyclerView(holder.binding.recyclerView, carouselScrollStates[position])
        }
    }

    private fun bindCarouselDoubleItems(holder: CarouselDoubleItemsViewHolder, position: Int) {
        val item = getItem(position) as GeneralScreenItem
        val limit = item.infoPages.size
        with(holder.adapter) {
            items = (if (limit > 0) item.infoPages.take(limit) else item.infoPages).chunked(2)
            onItemClickListener = onArticleClickListener
        }

        with(holder.binding) {

            bodyTitle = item.item.title
            bodyDescription = item.item.description
            // buttonTitle = item.item.firstButtonTitle
            // buttonAccessibilityLabel = item.item.firstAccessibilityLabel

            fullWidth = true
            hideFooter = true
            executePendingBindings()

            generalItemCarouselDoubleContainer.footerButtonView.setOnClickListener {
                navigateToLinkedCategoryClickListener(item.item)
            }
        }

        holder.binding.viewPager.adapter = holder.adapter
        holder.binding.viewPager.initializeWithAdapter(holder.adapter)
        with(holder.binding.carouselIndicator) {
            addAfterPageSwitchListener { carouselScrollStates[position] = it }
            setupWithViewPager2(holder.binding.viewPager, carouselScrollStates[position])
        }
    }

    private fun bindArticlesHeaderViewHolder(
        holder: ArticleListHeaderViewHolder, screen: GeneralScreen
    ) {
        screen as GeneralScreenItem
        with(holder.binding) {
            title = screen.item.title
            executePendingBindings()
        }
    }

    private fun bindArticleItemViewHolder(holder: ArticleItemViewHolder, screen: GeneralScreen) {
        screen as GeneralInfoItem
        with(holder.binding) {
            val infoPage = screen.item.infoPage

            imageUrl = infoPage.imageUrl
            bodyTitle = infoPage.title
            bodyDescription = infoPage.description
            categoryLabel = infoPage.categoryLabel

            executePendingBindings()

            root.setOnClickListener { onArticleClickListener(screen.item.infoPage) }
        }
    }


    private fun bindArticleItemHorizontalViewHolder(
        holder: ArticleItemHorizontalViewHolder,
        screen: GeneralScreen
    ) {
        screen as GeneralInfoItem
        with(holder.binding) {
            val article = screen.item.infoPage

            imageUrl = article.imageUrl
            bodyTitle = article.title
            bodyDescription = article.description
            categoryLabel = article.categoryLabel

            imageUrl = article.imageUrl
            bodyTitle = article.title
            bodyDescription = article.description
            categoryLabel = article.categoryLabel
            chevron1.isVisible = true
            description1.maxLines = 2
            description1.ellipsize = TextUtils.TruncateAt.END

            executePendingBindings()

            root.setOnClickListener { onArticleClickListener(article) }
        }
    }


    private fun bindHeroItemViewHolder(holder: HeroItemViewHolder, screen: GeneralScreen) {
        screen as GeneralInfoItem
        with(holder.binding) {
            val infoPage = screen.item.infoPage

            imageUrl = infoPage.imageUrl
            bodyTitle = infoPage.title
            bodyDescription = infoPage.description
            categoryLabel = infoPage.categoryLabel

            executePendingBindings()

            root.setOnClickListener { onArticleClickListener(screen.item.infoPage) }
        }
    }

    private fun ViewPager2.initializeWithAdapter(viewPagerAdapter: RecyclerView.Adapter<*>) {
        adapter = viewPagerAdapter
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 3
        setPageTransformer(BHCarouselOffsetPageTransformer(12.dpToPx(context), 10.dpToPx(context)))
    }

    private class HeroItemViewHolder(val binding: BhDiscoverHeroBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class CarouselSmallItemsViewHolder(val binding: BhCarouselSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { CarouselSmallItemsAdapter() }
    }

    private class CarouselLargeItemsViewHolder(val binding: BhCarouselLargeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { CarouselLargeItemsAdapter() }
    }

    private class CarouselDoubleItemsViewHolder(val binding: BhCarouselDoubleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { CarouselDoubleItemsAdapter() }
    }

    private class ArticleListHeaderViewHolder(val binding: BhArticleListItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ArticleItemViewHolder(val binding: BhArticleListItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ArticleItemHorizontalViewHolder(val binding: BhArticleListItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<GeneralScreen>() {
            override fun areItemsTheSame(oldItem: GeneralScreen, newItem: GeneralScreen) =
                oldItem == newItem

            override fun areContentsTheSame(old: GeneralScreen, new: GeneralScreen) = old == new

        }
    }
}
