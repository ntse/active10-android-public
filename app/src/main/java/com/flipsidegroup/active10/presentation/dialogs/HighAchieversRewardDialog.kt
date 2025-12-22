package com.flipsidegroup.active10.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.databinding.DialogHighAchieversRewardBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl

class HighAchieversRewardDialog(
    private val rewardBadge: RewardBadge,
    private val onDismiss: () -> Unit
): ExpandedBottomSheetDialogFragment() {

    private var binding: DialogHighAchieversRewardBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_high_achievers_reward, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogHighAchieversRewardBinding.bind(view)

        binding.dialogHighAchieversRewardIcon.loadFromUrl(rewardBadge.onImage)

        ViewCompat.setAccessibilityHeading(binding.dialogHighAchieversRewardTitle, true)
        binding.dialogHighAchieversRewardTitle.requestFocus()
        binding.dialogHighAchieversRewardTitle.text = rewardBadge.title

        binding.dialogHighAchieversRewardSubtitle.text = rewardBadge.text

        binding.dialogHighAchieversRewardCloseButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog).also {
            onDismiss()
        }
    }
}
