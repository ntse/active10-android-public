package com.flipsidegroup.active10.presentation.todaywalk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentTodayWalkPermissionBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.PermissionClickListener
import com.flipsidegroup.active10.utils.TargetHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

class TodayWalkPermissionFragment : BaseFragment<BaseView>() {

    companion object {
        fun newInstance() = TodayWalkPermissionFragment()
    }

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    private var permissionClickListener: PermissionClickListener? = null

    private var binding: FragmentTodayWalkPermissionBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today_walk_permission, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PermissionClickListener) {
            permissionClickListener = context
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodayWalkPermissionBinding.bind(view)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        TargetHelper.initTargetIfEmpty(settingsUtils)

        setUpToolbar()
        setUpViews()
        binding.permissionHeaderTV.setHeading()
    }

    private fun setUpToolbar() {
        setUpSupportToolbar(R.string.bottom_nav_menu_today_walk)
    }

    private fun setUpViews() {
        binding.horseShoePB.shouldDrawMinMax = false
        binding.continueBTN.setOnClickListenerWithDebounce {
            permissionClickListener?.onButtonClicked()
        }

        positionWalkAnimation()
    }

    private fun positionWalkAnimation() {
        val walkAnimationSize = UIUtils.getDimension(R.dimen.walk_animation_size)
        val layoutParams = binding.walkingLAV.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin =
            -walkAnimationSize.toInt() / 2 - (walkAnimationSize.toInt() / 3) / 2
        binding.walkingLAV.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()
        binding.toolbar.toolbarCenterTV.announceHeader()
    }
}

