package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentCheckboxBinding
import com.phe.betterhealth.widgets.checkbox.BHCheckBoxLayout

class CheckboxDemo : Fragment(R.layout.fragment_checkbox) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCheckboxBinding.bind(view)

        binding.stateToggle.setOnClickListener {
            binding.layout1.toggleErrorMessage()
            binding.layout2.toggleErrorMessage()
            binding.layout3.toggleErrorMessage()
            binding.layout4.toggleErrorMessage()
        }
    }

    private fun BHCheckBoxLayout.toggleErrorMessage() {
        errorMessage = if (errorMessage == null) "This is an error" else null
    }
}
