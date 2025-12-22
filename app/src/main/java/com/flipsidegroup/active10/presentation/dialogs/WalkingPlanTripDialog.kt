package com.flipsidegroup.active10.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogWalkingPlanTripBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl

class WalkingPlanTripDialog(
    private val contents: List<ScreenContent>
) : DialogFragment() {

    private var binding: DialogWalkingPlanTripBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_walking_plan_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWalkingPlanTripBinding.bind(view)

        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setScreenContent(contents[0])

        var index = 0
        with(binding) {
            firstButton.setOnClickListener {
                index += 1
                setScreenContent(contents[index])
                if (index == contents.size - 1) {
                    firstButton.visibility = View.GONE
                }
            }
            secondButton.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setScreenContent(screenContent: ScreenContent) {
        with(binding) {
            content = screenContent
            image.loadFromUrl(screenContent.firstImageUrl)
            if (screenContent.secondButtonTitle.isNullOrBlank()) {
                secondButton.text = getString(R.string.close_button)
            }
        }
    }
}