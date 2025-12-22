package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentCardBinding
import com.phe.betterhealth.widgets.card.BHCardView

class CardDemo : Fragment(R.layout.fragment_card) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCardBinding.bind(view)

        binding.container.children
            .filter { it is BHCardView }
            .map { it as BHCardView }
            .toList()
            .forEach { card ->
                card.footerButtonView.setOnClickListener { showToast(it as TextView) }
            }
    }

    private fun showToast(textView: TextView) {
        Toast.makeText(requireContext(), textView.text, Toast.LENGTH_SHORT)
            .show()
    }
}
