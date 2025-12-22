package com.flipsidegroup.active10.presentation.pacechecker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.flipsidegroup.active10.databinding.ActivityPaceCheckerBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.replaceFragment
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

class PaceCheckerActivity : BaseSecureActivity<BaseView>() {

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    private lateinit var binding: ActivityPaceCheckerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaceCheckerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
    }

    fun navigateToFragments(fragment: Fragment, backStackTag: String? = null) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            replaceFragment(
                addToBackStack = true,
                viewId = binding.paceCheckerFragmentContainer.id,
                fragment = fragment,
                backStackTag = backStackTag
            )
        }
    }
}

fun Context.getPaceCheckerIntent() = Intent(this, PaceCheckerActivity::class.java)

