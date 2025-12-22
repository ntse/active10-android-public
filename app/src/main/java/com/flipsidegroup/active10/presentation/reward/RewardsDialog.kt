package com.flipsidegroup.active10.presentation.reward

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.databinding.LayoutRewardsDialogBinding
import com.flipsidegroup.active10.presentation.dialogs.ExpandedBottomSheetDialogFragment
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearIntent
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.playAnimationIfEnabled
import com.phe.betterhealth.widgets.utils.textColor
import timber.log.Timber
import java.util.Locale

class RewardsDialog : ExpandedBottomSheetDialogFragment() {

    val earnedBadges: MutableList<EarnRewardBadge> = mutableListOf()
    var isAnimationEnabled: Boolean = true
    var rewardBadge: RewardBadge? = null

    private var binding: LayoutRewardsDialogBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            isAnimationEnabled = bundle.getBoolean(ANIMATION_ENABLED)
            rewardBadge = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    bundle.getParcelable(REWARD_BADGE, RewardBadge::class.java)
                }

                else -> {
                    bundle.getParcelable(REWARD_BADGE)
                }
            }

            val counts = bundle.getInt(EARN_REWARD_COUNT)
            for (index in 0 until counts) {
                val key = String.format(Locale.UK, EARN_REWARD_BADGES, index)
                val item = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        bundle.getSerializable(key, EarnRewardBadge::class.java)
                    }

                    else -> {
                        bundle.getSerializable(key) as EarnRewardBadge
                    }
                }

                if (item != null) {
                    earnedBadges.add(item)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_rewards_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutRewardsDialogBinding.bind(view)

        if (rewardBadge == null) {
            return
        }

        val badge = rewardBadge!!

        val earnRewardBadge = earnedBadges.find { it.id == badge.id }
        binding.badgeTitle.text = badge.title

        if (earnRewardBadge == null) {
            binding.badgeDescription.text = badge.howTo
            binding.badgeIcon.loadFromUrl(badge.offImage)
            dynamicNumber(badge, isAnimated = false, isOffImage = true)
        } else {
            if (isAnimationEnabled) {
                binding.badgeLottie.isVisible = true
                binding.badgeIcon.isVisible = false

                binding.badgeLottie.setFailureListener {
                    Timber.w("Cannot load an animation")

                    binding.badgeLottie.isVisible = false
                    binding.badgeIcon.isVisible = true
                    binding.badgeIcon.loadFromUrl(badge.onImage, isTransition = true)
                    dynamicNumber(badge, isAnimated = false, isOffImage = false)
                }

                binding.badgeLottie.setAnimationFromUrl(badge.animation)
                binding.badgeLottie.playAnimationIfEnabled(isAnimationEnabled)
                dynamicNumber(badge, isAnimated = true, isOffImage = false)
            } else {
                binding.badgeLottie.isVisible = false
                binding.badgeIcon.isVisible = true
                binding.badgeIcon.loadFromUrl(badge.onImage, isTransition = true)
                dynamicNumber(badge, isAnimated = false, isOffImage = false)
            }
            binding.badgeDescription.text = badge.text
            binding.badgeEarnedTimeTv.text = getString(R.string.earned_badge_time, DateHelper.formatBadgeDate(earnRewardBadge.timestamp))
        }

        binding.share.isVisible = earnRewardBadge != null
        binding.share.setOnClickListener {
            shareBadge(badge)
        }

        binding.goJauntlyButton.isVisible = badge.slug == "goJauntly" && earnRewardBadge == null
        binding.goJauntlyButton.setOnClickListener {
            startActivity(requireContext().WalksNearIntent())
            dismiss()
            requireActivity().finish()
        }

        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun dynamicNumber(reward: RewardBadge, isAnimated: Boolean, isOffImage: Boolean) {
        if (reward.slug != RewardBadgeEnum.TARGET_HITTER.slug) return
        if (reward.repetitions == 0) return

        with(binding) {
            numberTV.isVisible = reward.slug == RewardBadgeEnum.TARGET_HITTER.slug
            numberTV.text = reward.repetitions.toString()

            if (isOffImage) {
                numberTV.textColor = R.color.light_grey_two
                return
            } else {
                numberTV.textColor = R.color.white
            }

            if (isAnimated) {
                numberTV.scaleX = 0.1f
                numberTV.scaleY = 0.1f

                val scaleXAnimator = ObjectAnimator.ofFloat(numberTV, "scaleX", 1f)
                val scaleYAnimator = ObjectAnimator.ofFloat(numberTV, "scaleY", 1f)

                val animatorSet = AnimatorSet().apply {
                    playTogether(scaleXAnimator, scaleYAnimator)
                    duration = 600
                    startDelay = 400
                }

                animatorSet.start()
            }
        }
    }

    private fun shareBadge(rewardBadge: RewardBadge) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = SHARING_INTENT_TYPE
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

        val shareMessage = rewardBadge.share + " " + SHARING_URL
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.share_intent_chooser_title)
            )
        )
    }

    companion object {

        const val EARN_REWARD_BADGES = "EARN_REWARD_BADGES_%d_KEY"
        const val EARN_REWARD_COUNT = "EARN_REWARD_COUNT_KEY"
        const val REWARD_BADGE = "REWARD_BADGE_KEY"
        const val ANIMATION_ENABLED = "ANIMATION_ENABLED_KEY"

        fun newInstance(
            earnedBadges: List<EarnRewardBadge>,
            rewardBadge: RewardBadge,
            isAnimationEnabled: Boolean
        ): RewardsDialog {
            val dialog = RewardsDialog()

            val bundle = Bundle()
            bundle.putBoolean(ANIMATION_ENABLED, isAnimationEnabled)
            bundle.putParcelable(REWARD_BADGE, rewardBadge)
            bundle.putInt(EARN_REWARD_COUNT, earnedBadges.size)
            earnedBadges.forEachIndexed { index, element ->
                bundle.putSerializable(String.format(Locale.UK, EARN_REWARD_BADGES, index), element)
            }

            dialog.arguments = bundle

            return dialog
        }
    }
}