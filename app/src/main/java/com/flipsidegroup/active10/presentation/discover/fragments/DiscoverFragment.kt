package com.flipsidegroup.active10.presentation.discover.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentDiscoverBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralScreen
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralScreenAdapter
import com.flipsidegroup.active10.presentation.discover.presenter.DiscoverPresenter
import com.flipsidegroup.active10.presentation.discover.view.DiscoverView
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.utils.closeKeyboard
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import com.google.android.material.tabs.TabLayout
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

class DiscoverFragment : BaseFragment<DiscoverView>(), DiscoverView {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    @Inject
    internal lateinit var presenter: DiscoverPresenter

    private var binding: FragmentDiscoverBinding by lifecycleAwareVariable()
    private var searchText: String? = null

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    private val adapter by lazy {
        GeneralScreenAdapter().apply {
            onArticleClickListener = {
                val source =
                    if (searchText.isNullOrBlank()) "discover_tab_${
                        it.categoryLabel.replace(" ", "_").lowercase()
                    }"
                    else "discover_search"
                startActivity(
                    requireContext().DiscoverDetailsIntent(it.id, source, searchText)
                )
            }
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<DiscoverView> = presenter

    override fun onResume() {
        super.onResume()

        binding.discoverToolbar.requestFocus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDiscoverBinding.bind(view)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        initializeRecyclerView()
        presenter.loadData()
        binding.discoverToolbar.setHeading()
        initializeObservers()
    }

    private fun initializeObservers() {
        binding.discoverSearchCancel.setOnClickListener {
            binding.discoverSearchEdit.setText("")
            binding.discoverSearchEdit.clearFocus()
            presenter.enterSearchText("")
            requireActivity().closeKeyboard()
            searchText = ""
        }
        binding.discoverSearchEdit.addTextChangedListener {
            val text = it.toString()
            if (text.isBlank()) {
                binding.discoverEmptyStateText.isVisible = false
                binding.discoverTabs.isVisible = true
                binding.discoverSearchCancel.isVisible = false
            } else {
                binding.discoverTabs.isVisible = false
                binding.discoverSearchCancel.isVisible = true
            }
            presenter.enterSearchText(text)
            searchText = text
        }
        presenter.enterSearchText("")
    }

    override fun showLoading() {
        binding.discoverLoadingView.isVisible = true
    }

    override fun hideLoading() {
        binding.discoverLoadingView.isVisible = false
    }

    private fun initializeRecyclerView() {
        binding.discoverRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.discoverRecyclerView.adapter = adapter
    }

    override fun showData(data: List<GeneralScreen>, showEmptyState: Boolean) {
        adapter.submitList(data)
        binding.discoverEmptyStateText.isVisible = showEmptyState
        binding.discoverRecyclerView.isVisible = !showEmptyState
    }

    override fun showTabs(tabs: List<ScreenContent>) {
        binding.run {
            if (discoverTabs.tabCount == 0) {
                tabs.forEach {
                    val tab = discoverTabs.newTab()
                    tab.text = it.title.uppercase()
                    discoverTabs.addTab(tab)
                }
                setupTabsListener(discoverTabs, tabs)
            }
        }
    }

    private fun setupTabsListener(discoverTabs: TabLayout, tabs: List<ScreenContent>) {
        discoverTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    presenter.selectTab(tabs[it.position])
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    fun showMentalHealthTab() {
        for (i in 0 until binding.discoverTabs.tabCount) {
            if (binding.discoverTabs.getTabAt(i)?.text?.toString()
                    ?.lowercase() == "mental health"
            ) {
                binding.discoverTabs.getTabAt(i)?.select()
                return
            }
        }
    }

    fun showBetterHealthTab() {
        for (i in 0 until binding.discoverTabs.tabCount) {
            if (binding.discoverTabs.getTabAt(i)?.text?.toString()
                    ?.lowercase() == "better health"
            ) {
                binding.discoverTabs.getTabAt(i)?.select()
                return
            }
        }
    }

    fun showTab(position: Int, articleSlug: String? = null) {
        binding.discoverTabs.getTabAt(position)?.select()

        articleSlug?.let {
            startActivity(
                requireContext().DiscoverDetailsIntent(
                    source = position.toString(),
                    articleSlug = articleSlug
                )
            )
        }
    }
}
