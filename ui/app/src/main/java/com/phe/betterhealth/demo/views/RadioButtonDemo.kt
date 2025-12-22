package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentRadioButtonBinding
import com.phe.betterhealth.widgets.radiobutton.BHRadioGroup

class RadioButtonDemo : Fragment(R.layout.fragment_radio_button) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRadioButtonBinding.bind(view)

        binding.stateToggle.setOnClickListener {
            binding.layout1.toggleErrorMessage()
        }
    }

    private fun BHRadioGroup.toggleErrorMessage() {
        errorMessage = if (errorMessage == null) "This is an error" else null
    }
}
