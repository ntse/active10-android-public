package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.BannerItem
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentBannerBinding
import com.phe.betterhealth.widgets.banner.BHBannerIconType

class BannerDemo : Fragment(R.layout.fragment_banner) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBannerBinding.bind(view)
        with(binding) {
            bannerItem = BannerItem(
                infoText = "Info text first banner",
                infoTextColor = ContextCompat.getColor(requireContext(), R.color.bhWhite),
                imageSrc = "https://us.123rf.com/450wm/arcady31/arcady311207/arcady31120700010/14318388-be-happy-smiley.jpg",
                iconColor = ContextCompat.getColor(requireContext(), R.color.bhWhite),
                iconType = BHBannerIconType.INDICATOR
            )
            bannerItemTwo = BannerItem(
                infoText = "Info text second banner",
                infoTextColor = ContextCompat.getColor(requireContext(), R.color.bhCheckboxError),
                imageSrc = "https://us.123rf.com/450wm/arcady31/arcady311207/arcady31120700010/14318388-be-happy-smiley.jpg",
                iconColor = ContextCompat.getColor(requireContext(), R.color.bhWhite),
                iconType = BHBannerIconType.NONE
            )
            bannerItemThree = BannerItem(
                infoText = "Info text third banner",
                infoTextColor = ContextCompat.getColor(requireContext(), R.color.bhCheckboxError),
                imageSrc = "https://us.123rf.com/450wm/arcady31/arcady311207/arcady31120700010/14318388-be-happy-smiley.jpg",
                iconColor = ContextCompat.getColor(requireContext(), R.color.bhWhite),
                iconType = BHBannerIconType.CLOSE
            )
        }
    }
}
