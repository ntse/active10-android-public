package com.flipsidegroup.active10.presentation.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.FragmentSignInInfoBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.TitleDescriptionItem
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setTextHtml

class SignInInfoFragment : BaseFragment<BaseView>() {

    private var screenContent: ScreenContent? = null

    private var binding: FragmentSignInInfoBinding by lifecycleAwareVariable()

    companion object {
        fun newInstance(content: ScreenContent): SignInInfoFragment {
            val instance = SignInInfoFragment()
            instance.arguments = Bundle().apply {
                putParcelable(Constants.SIGN_IN_INFO_CONTENT, content)
            }
            return instance
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInInfoBinding.bind(view)
        binding.signInInfoToolbar.backTV.setOnClickListener { requireActivity().onBackPressed() }

        initializeContent()
    }

    private fun initializeContent() {
        screenContent = arguments?.let {
            BundleCompat.getParcelable(
                it,
                Constants.SIGN_IN_INFO_CONTENT,
                ScreenContent::class.java
            )
        }
        showContentInViews()
    }

    private fun showContentInViews() {
        screenContent?.let {
            binding.signInInfoTitle.setTextHtml(it.title)
            binding.signInInfoTipLayout.description.setTextHtml(it.description)
            binding.signInInfoTipLayout.image.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_green_shield)
            )
            binding.signInInfoLL.removeAllViews()
            it.properties.forEachIndexed { index, screenProperty ->
                if (Regex("^reason_\\d+_text$").matches(screenProperty.key)) {
                    val data = screenProperty.value.split("\r\n")
                    binding.signInInfoLL.addView(
                        TitleDescriptionItem(requireContext()).apply {
                            findViewById<TextView>(R.id.itemTitle).setTextHtml(data[0])
                            findViewById<TextView>(R.id.itemDescription).setTextHtml(data[1])
                        }
                    )
                }
            }
        }
    }

}