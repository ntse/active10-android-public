package com.flipsidegroup.active10.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogHeroPopupBinding
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import timber.log.Timber

class HeroPopupDialog(
    private val screenContent: ScreenContent,
    private val onContinue: (ScreenContent) -> Unit,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper
) : ExpandedBottomSheetDialogFragment() {

    private var binding: DialogHeroPopupBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_hero_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogHeroPopupBinding.bind(view)

        ViewCompat.setAccessibilityHeading(binding.dialogHeroTitle, true)

        screenContent.media[0]?.url?.let {
            binding.dialogHeroImage.loadFromUrl(it)
        }
        binding.dialogHeroImage.isVisible = screenContent.media.isNotEmpty()

        binding.dialogHeroTitle.text = screenContent.title
        binding.dialogHeroSubtitle.text = screenContent.description

        binding.dialogHeroHeading.text = screenContent.getPropertyValue("heading") ?: ""
        binding.dialogHeroHeading.isVisible = !binding.dialogHeroHeading.text.isNullOrEmpty()

        binding.dialogHeroContinueButton.text = screenContent.getPropertyValue("continue") ?: getString(R.string.button_continue)
        binding.dialogHeroContinueButton.setOnClickListener {
            dismiss()
            try {
                onContinue(screenContent)
            } catch (err: Throwable) {
                Timber.e(err)
            }
        }

        screenContent.getPropertyValue("later").let {
            binding.dialogHeroLaterButton.text = it
            binding.dialogHeroLaterButton.isVisible = !it.isNullOrEmpty()
            binding.dialogHeroLaterButton.setOnClickListener {
                firebaseAnalyticsHelper.popupCanceled(screenContent.slug)
                dismiss() }
        }
    }
}
