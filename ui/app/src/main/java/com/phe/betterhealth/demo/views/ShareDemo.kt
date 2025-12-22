package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phe.betterhealth.components.share.ShareButton
import com.phe.betterhealth.components.share.ShareButtonExtendedStyle
import com.phe.betterhealth.components.share.ShareButtonStyle
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentShareBinding

class ShareDemo : Fragment(R.layout.fragment_share) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShareBinding.bind(view)

        with(binding) {
            button1 = Button(
                shareButtonStyle = ShareButtonStyle.EXTENDED.value,
                shareButtonTitle = "Button 1",
                shareButtonDescription = "This is a description of button 1",
                shareButtonAccessibilityLabel = "LABEL"
            )
            button2 = Button(
                shareButtonStyle = ShareButtonStyle.STANDARD.value,
                shareButtonTitle = "Button 2",
                shareButtonDescription = "hidden",
                shareButtonAccessibilityLabel = "LABEL"
            )
            button3 = Button(
                shareButtonStyle = ShareButtonStyle.EXTENDED.value,
                shareButtonTitle = "Button 1",
                shareButtonDescription = "This is a description of button 1",
                shareButtonAccessibilityLabel = "LABEL",
                shareButtonExtendedStyle = ShareButtonExtendedStyle.SECONDARY,
            )
            onClick = View.OnClickListener {
                Toast.makeText(requireContext(), "CLICK!", Toast.LENGTH_SHORT).show()
            }
            executePendingBindings()
        }
    }

    data class Button(
        override val shareButtonStyle: String,
        override val shareButtonTitle: String,
        override val shareButtonDescription: String,
        override val shareButtonAccessibilityLabel: String,
        override val shareButtonExtendedStyle: ShareButtonExtendedStyle = ShareButtonExtendedStyle.PRIMARY,
    ) : ShareButton
}
