package com.flipsidegroup.active10.presentation.home.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import coil.load
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.databinding.DialogDeepLinkBinding
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.presenter.DeepLinkPresenter
import com.flipsidegroup.active10.presentation.home.view.DeepLinkView
import com.flipsidegroup.active10.utils.openURL
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DeepLinkDialog : DialogFragment(R.layout.dialog_deep_link), DeepLinkView {
    private lateinit var binding: DialogDeepLinkBinding

    @Inject
    internal lateinit var presenter: DeepLinkPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeepLinkBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidSupportInjection.inject(this)

        presenter.bind(this)
        presenter.loadScreenData()

        setUpDialogView()
        initListeners()
    }

    private fun setUpDialogView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroy() {
        presenter.unbind()
        super.onDestroy()
    }

    private fun initListeners() {
        binding.firstBtn.setOnClickListener {
            val externalLink = presenter.getExternalLink()

            if (externalLink.isNullOrBlank()) {
                navigateToInternalRoute()
            } else {
                requireContext().openURL(externalLink)
            }

            dismiss()
        }

        binding.secondBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onScreenLoaded(
        title: String,
        iconUrl: String?,
        description: String,
        firstButtonTitle: String,
        secondButtonTitle: String
    ) {
        binding.iconIV.load(iconUrl)
        binding.titleTV.text = title
        binding.contentTV.text = description
        binding.firstBtn.text = firstButtonTitle
        binding.secondBtn.text = secondButtonTitle
    }

    private fun navigateToInternalRoute() {
        val tabToSelect = presenter.validateInternalLinkAndGetHomeScreenTab()
        if (tabToSelect != null) {
            launchHomeActivity(tabToSelect)

            val articleIdToOpen = presenter.parseArticleIdFromInternalLink()
            val articleSlugToOpen = presenter.parseArticleSlugFromInternalLink()

            launchArticleActivity(articleId = articleIdToOpen, articleSlug = articleSlugToOpen)
        }
    }

    private fun launchHomeActivity(tabToSelect: Int) {
        startActivity(
            HomeActivity.getHomeIntent(
                context = requireContext(),
                isForeground = true,
                screenPosition = tabToSelect,
                myWalkType = PeriodTypeEnum.DAYS
            )
        )
    }

    private fun launchArticleActivity(articleId: Long?, articleSlug: String?) {
        startActivity(
            requireContext().DiscoverDetailsIntent(
                articleId = articleId,
                source = SOURCE_DEEP_LINK_DIALOG,
                searchText = "",
                articleSlug = articleSlug
            )
        )
    }

    companion object {
        private const val SOURCE_DEEP_LINK_DIALOG = "deep_link_dialog"
    }
}