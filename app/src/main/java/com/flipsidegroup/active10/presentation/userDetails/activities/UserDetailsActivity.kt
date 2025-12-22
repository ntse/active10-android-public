package com.flipsidegroup.active10.presentation.userDetails.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.databinding.ActivityUserDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.userDetails.fragments.UserDetailsFragment
import com.flipsidegroup.active10.presentation.userDetails.presenter.UserDetailsPresenter
import com.flipsidegroup.active10.presentation.userDetails.view.UserDetailsView
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.replaceFragment
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.UserDetailsActivityIntent(): Intent {
    return Intent(this, UserDetailsActivity::class.java)
}

class UserDetailsActivity : BaseSecureActivity<UserDetailsView>(), UserDetailsView {

    @Inject
    internal lateinit var presenter: UserDetailsPresenter

    private var binding: ActivityUserDetailsBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<UserDetailsView>? = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityUserDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        presenter.getAboutYouDescription()
    }

    override fun onAboutYouDescriptionReceived(onboarding: Onboarding?) {
        hideLoading()
        replaceFragment(
            viewId = R.id.container,
            fragment = UserDetailsFragment.newInstance(
                UserDetailsFragment.DEFAULT_INDEX,
                onboarding?.aboutYou.orEmpty(),
            )
        )
        val activitySubtitleTV = findViewById<TextView>(R.id.activitySubtitleTV)
        val ageSubtitleTV = findViewById<TextView>(R.id.ageSubtitleTV)
        val genderSubtitleTV = findViewById<TextView>(R.id.genderSubtitleTV)
        ViewCompat.setAccessibilityHeading(genderSubtitleTV, true)
        ViewCompat.setAccessibilityHeading(ageSubtitleTV, true)
        ViewCompat.setAccessibilityHeading(activitySubtitleTV, true)
    }
}
