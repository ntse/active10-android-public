package com.phe.betterhealth.components.moodbottomdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.databinding.BhMoodBottomDialogBinding
import com.phe.betterhealth.widgets.utils.nullableSerializable
import com.phe.betterhealth.widgets.utils.serializable

/**
 * This view has few property which help You customize dialog.
 *
 * @property titleText - title text of dialog
 * @property subtitleText - subtitle text of dialog
 * @property descriptionText - description text of dialog
 * @property buttonText - text visible on close button
 *
 * @property onArticleClickListener - action after click on related article in dialog
 * @property onButtonClickListener - additional action after click on close button in dialog (dismiss() is always called)
 *
 * @property dialogType - dialog type - here You need to pass one of the enum from [BHMoodBottomDialogType] class
 *
 * @property article - here You need to pass [BHRelatedArticle] class - if this property is null then related article view is invisible
 */
class BHMoodBottomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: BhMoodBottomDialogBinding

    var onArticleClickListener: () -> Unit = {}
    var onButtonClickListener: () -> Unit = {}
    var article: BHRelatedArticle? = null
    var titleText: String? = null
    var subtitleText: String? = null
    var descriptionText: String? = null
    var buttonText: String? = null
    var dialogType: BHMoodBottomDialogType = BHMoodBottomDialogType.GOOD

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BhMoodBottomDialogBinding.inflate(inflater, container, false)

        val resumeMode = savedInstanceState?.getBoolean(KEY_RESUME) ?: false

        if (resumeMode) {
            article = savedInstanceState?.serializable<BHRelatedArticle>(KEY_ARTICLE)
            dialogType = savedInstanceState?.nullableSerializable<BHMoodBottomDialogType>(KEY_TYPE)
                ?: BHMoodBottomDialogType.GOOD

            titleText = savedInstanceState?.getString(KEY_TITLE)
            subtitleText = savedInstanceState?.getString(KEY_SUBTITLE)
            descriptionText = savedInstanceState?.getString(KEY_DESCRIPTION)
            buttonText = savedInstanceState?.getString(KEY_BUTTON)
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        article?.let {
            outState.putSerializable(KEY_ARTICLE, it)
        }

        outState.putSerializable(KEY_TYPE, dialogType)
        outState.putString(KEY_TITLE, titleText)
        outState.putString(KEY_SUBTITLE, subtitleText)
        outState.putString(KEY_DESCRIPTION, descriptionText)
        outState.putString(KEY_BUTTON, buttonText)
        outState.putBoolean(KEY_RESUME, true)
    }

    override fun getTheme() =
        R.style.Widget_BetterHealth_MoodBottomDialogStyle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            bhMoodBottomDialogCloseButton.setOnClickListener {
                dismiss()
                onButtonClickListener()
            }
            bhMoodBottomDialogArticle.articleItemRootView.setOnClickListener {
                onArticleClickListener()
            }
            relatedArticle = article
            bhMoodBottomDialogTitle.text = titleText.orEmpty()
            bhMoodBottomDialogSubtitle.text = subtitleText.orEmpty()
            bhMoodBottomDialogDescription.text = descriptionText.orEmpty()
            bhMoodBottomDialogCloseButton.text = buttonText.orEmpty()

            if (article?.isLink == true) {
                bhMoodBottomDialogArticle.root.contentDescription =
                    "${article?.categoryLabel.orEmpty()}, ${article?.title.orEmpty()}, this button will open link in external browser"
            }

            val drawableRes = when (dialogType) {
                BHMoodBottomDialogType.GREAT -> R.drawable.ic_mood_great
                BHMoodBottomDialogType.GOOD -> R.drawable.ic_mood_good
                BHMoodBottomDialogType.OK -> R.drawable.ic_mood_ok
                BHMoodBottomDialogType.TIRED -> R.drawable.ic_mood_tired
            }
            bhMoodBottomDialogIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    drawableRes
                )
            )

            ViewCompat.setAccessibilityDelegate(
                bhMoodBottomDialogTitle,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.isHeading = true
                    }
                })

            (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun showDialog(fragmentManager: FragmentManager) {
        show(fragmentManager, DIALOG_TAG)
    }

    companion object {
        const val KEY_RESUME = "resume_key"
        const val KEY_TITLE = "title_key"
        const val KEY_DESCRIPTION = "description_key"
        const val KEY_ARTICLE = "article_key"
        const val KEY_SUBTITLE = "subtitle_key"
        const val KEY_BUTTON = "button_key"
        const val KEY_TYPE = "type_key"
        const val DIALOG_TAG = "BHMoodBottomDialog"
    }
}
