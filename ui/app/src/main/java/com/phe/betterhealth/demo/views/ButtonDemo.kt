package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentButtonBinding

class ButtonDemo : Fragment(R.layout.fragment_button) {

    private var number = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentButtonBinding.bind(view).container.children.filter { it is LinearLayout }.flatMap {
            (it as LinearLayout).children
        }.map { (it as Button) }.forEach {
            it.setOnClickListener { updateCounter() }
        }
    }

    private fun updateCounter() {
        view?.let {
            FragmentButtonBinding.bind(it).counter.text = (number++).toString()
        }
    }
}
