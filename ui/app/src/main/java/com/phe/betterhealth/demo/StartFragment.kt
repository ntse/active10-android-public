package com.phe.betterhealth.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.phe.betterhealth.demo.databinding.FragmentStartBinding

class StartFragment : Fragment(R.layout.fragment_start) {

    private val demos = listOf(
        "Button" to R.id.action_FirstFragment_to_buttonDemo,
        "CheckBox" to R.id.action_FirstFragment_to_checkboxDemo,
        "Input" to R.id.action_FirstFragment_to_inputDemo,
        "RadioButton" to R.id.action_FirstFragment_to_radioButtonDemo,
        "BottomNavigationView" to R.id.action_FirstFragment_to_bottomNavigationDemo,
        "Carousel" to R.id.action_FirstFragment_to_carouselDemo,
        "Card" to R.id.action_FirstFragment_to_cardDemo,
        "Pager" to R.id.action_FirstFragment_to_pagerDemo,
        "Share" to R.id.action_FirstFragment_to_shareDemo,
        "Banner" to R.id.action_FirstFragment_to_bannerDemo,
        "MoodSelector" to R.id.action_FirstFragment_to_moodSelectorDemo,
        "MoodBottomDialog" to R.id.action_FirstFragment_to_moodBottomDialogDemo,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStartBinding.bind(view)

        val demoAdapter = DemoAdapter().apply {
            items = demos
            onItemClickListener = { findNavController().navigate(it) }
        }

        with(binding.recycler) {
            adapter = demoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
