package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentInputBinding
import com.phe.betterhealth.widgets.textfield.BHSpinnerAdapter
import com.phe.betterhealth.widgets.textfield.BHTextInputLayout

class InputDemo : Fragment(R.layout.fragment_input) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInputBinding.bind(view)

        binding.spinner1.setAdapter(
            BHSpinnerAdapter(
                context = requireContext(),
                objects = listOf(
                    "Item 1",
                    "Item 2",
                    "Item 30000",
                )
            )
        )

        binding.stateToggle.setOnClickListener {
            binding.field1.toggleError()
            binding.field2.toggleError()
            binding.field3.toggleError()
            binding.field4.toggleError()
        }
    }

    private fun BHTextInputLayout.toggleError() {
        isErrorEnabled = !isErrorEnabled
        errorMessage = if (errorMessage.isNullOrBlank()) "This is an error" else ""
    }
}
