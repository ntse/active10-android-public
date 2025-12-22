package com.flipsidegroup.active10.presentation.howitworks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.databinding.FragmentHowItWorksBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setHeading

private const val IN_HOW_IT_WORKS_ITEM = "IN_HOW_IT_WORKS_ITEM"

class HowItWorksFragment : Fragment() {

    private var binding: FragmentHowItWorksBinding by lifecycleAwareVariable()

    companion object {
        fun newInstance(howItWorks: HowItWorks): HowItWorksFragment {
            val instance = HowItWorksFragment()
            instance.arguments = Bundle().apply {
                putParcelable(IN_HOW_IT_WORKS_ITEM, howItWorks)
            }
            return instance
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_how_it_works, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHowItWorksBinding.bind(view)

        setUpViews()
    }

    fun setUpViews() {
        val howItWorks = arguments?.getParcelable<HowItWorks>(IN_HOW_IT_WORKS_ITEM)
        howItWorks ?: return

        binding.titleTV.text = howItWorks.title
        binding.descriptionTV.text = howItWorks.description
        binding.howIV.loadFromUrl(howItWorks.image)
        binding.titleTV.setHeading()
    }
}
