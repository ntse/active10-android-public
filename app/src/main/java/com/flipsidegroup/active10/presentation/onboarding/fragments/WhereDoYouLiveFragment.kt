package com.flipsidegroup.active10.presentation.onboarding.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.get
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.FragmentWhereDoYouLiveBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.interfaces.WhereDoYouLiveInitListener
import com.flipsidegroup.active10.presentation.onboarding.interfaces.WhereDoYouLiveListener
import com.flipsidegroup.active10.presentation.onboarding.presenter.WhereDoYouLivePresenter
import com.flipsidegroup.active10.presentation.onboarding.view.WhereDoYouLiveView
import com.flipsidegroup.active10.utils.Constants.IN_IS_NEW_USER
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import javax.inject.Inject

class WhereDoYouLiveFragment : BaseFragment<WhereDoYouLiveView>(),
    WhereDoYouLiveView, WhereDoYouLiveListener {

    private var location: String = ""

    companion object {
        fun newInstance(isNewUser: Boolean = true): WhereDoYouLiveFragment {
            val instance = WhereDoYouLiveFragment()
            instance.arguments = Bundle().apply {
                putBoolean(IN_IS_NEW_USER, isNewUser)
            }
            return instance
        }
    }

    @Inject
    internal lateinit var presenter: WhereDoYouLivePresenter

    private var binding: FragmentWhereDoYouLiveBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<WhereDoYouLiveView> = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is WhereDoYouLiveInitListener) {
            context.initWhereDoYouLiveListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_where_do_you_live, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWhereDoYouLiveBinding.bind(view)

        presenter.getContent()
    }

    override fun onContentReceived(screenContent: ScreenContent?) =
        setContent(screenContent)

    override fun scrollDown() {
        binding.scrollContainer.fullScroll(View.FOCUS_DOWN)
    }

    override fun getCheckedLocation(): String {
        return location
    }

    private fun setContent(screenContent: ScreenContent?) {
        screenContent?.let {
            binding.currentLocationTitle.text = it.title
            binding.currentLocationSubtitle.text = it.description
            binding.currentLocationImage.loadFromUrl(it.firstImageUrl)
            it.getPropertyValue("locations")?.let { locations ->
                locations.split(",").forEachIndexed { index, location ->
                    val radioButton = View.inflate(
                        context,
                        R.layout.item_country,
                        null
                    ) as RadioButton
                    with(radioButton) {
                        text = location
                        id = index
                        width = binding.currentLocationContainer.measuredWidth
                        binding.currentLocationContainer.addView(this)
                    }
                }
            }
            binding.currentLocationContainer.setOnCheckedChangeListener { radioGroup, i ->
                location = (radioGroup[i] as RadioButton).text.toString()
            }
        }
        initLocation()
    }

    private fun initLocation() {
        val locationId = binding.currentLocationContainer.checkedRadioButtonId
        if (locationId == -1) return

        location = binding.currentLocationContainer
            .findViewById<RadioButton>(locationId)
            .text.toString()
    }

    private fun checkButtonIsEnabled() {

    }
}
