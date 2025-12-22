package com.flipsidegroup.active10.presentation.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.databinding.FragmentTipsBinding
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setHeading

private const val IN_TIP_ITEM = "IN_TIP_ITEM"

class TipsFragment : Fragment() {

    companion object {
        fun newInstance(tip: Tip): TipsFragment {
            val instance = TipsFragment()
            instance.arguments = Bundle().apply {
                putParcelable(IN_TIP_ITEM, tip)
            }
            return instance
        }
    }

    private var binding: FragmentTipsBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTipsBinding.bind(view)

        setUpViews()
    }

    override fun onResume() {
        super.onResume()
        binding.containerLL.requestFocus()
        binding.descriptionLL.requestFocus()
        binding.titleTV.announceHeader()
    }

    fun setUpViews() {
        val tip = arguments?.getParcelable<Tip>(IN_TIP_ITEM)
        tip ?: return

        binding.titleTV.text = tip.tipTitle
        binding.descriptionTV.text = tip.tipDescription
        binding.animationIV.loadFromUrl(tip.imageRes)
        binding.titleTV.setHeading()
    }
}
