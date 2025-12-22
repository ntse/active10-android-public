package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentMoodSelectorBinding

class MoodSelectorDemo : Fragment(R.layout.fragment_mood_selector) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMoodSelectorBinding.bind(view)
        binding.selector.setOnFirstAnswerClickListener({ showToast("dassdadsa") })
        binding.selector.titleText = "JAKIS TAM TEKST"
        binding.selector.subtitleText = "asdsda"
    }

    fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
