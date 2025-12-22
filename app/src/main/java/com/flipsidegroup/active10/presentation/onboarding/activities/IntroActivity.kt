package com.flipsidegroup.active10.presentation.onboarding.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.flipsidegroup.active10.data.NewUserEnum
import com.flipsidegroup.active10.databinding.ActivityIntroBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.adapters.IntroAdapter
import com.flipsidegroup.active10.presentation.onboarding.adapters.IntroItemType
import com.flipsidegroup.active10.presentation.onboarding.interfaces.WhereDoYouLiveInitListener
import com.flipsidegroup.active10.presentation.onboarding.interfaces.WhereDoYouLiveListener
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.utils.Constants.IN_IS_NEW_USER
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.OnPageChangeListener
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber

fun Context.IntroIntent(isNewUser: Boolean = true): Intent {
    return Intent(this, IntroActivity::class.java).apply {
        putExtra(IN_IS_NEW_USER, isNewUser)
    }
}

class IntroActivity : BaseSecureActivity<BaseView>(), WhereDoYouLiveInitListener {

    private var whereDoYouLiveListener: WhereDoYouLiveListener? = null
    private var isNewUser = true
    private var introAdapter: IntroAdapter? = null
    private var isUserLoggedInTemp: Boolean = false
    private var signInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                goToNextScreen()
            }
        }

    private var binding: ActivityIntroBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityIntroBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        binding.introToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.introToolbar.root.isVisible = false

        setUpViews()

        preferenceRepository.isPaceCheckerNewUser = true
    }

    override fun onBackPressed() {
        if (binding.introVP.currentItem == 0) {
            super.onBackPressed()
        } else {
            goToPreviousScreen()
        }
    }

    override fun initWhereDoYouLiveListener(whereDoYouLiveListener: WhereDoYouLiveListener) {
        this.whereDoYouLiveListener = whereDoYouLiveListener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                goToPreviousScreen()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        preferenceRepository.isUserLoggedIn.let {
            if (!isUserLoggedInTemp && it) {
                isUserLoggedInTemp = true
            }
        }
    }

    private fun setUpViews() {
        isNewUser = intent.getBooleanExtra(IN_IS_NEW_USER, true)

        introAdapter = IntroAdapter(supportFragmentManager, isNewUser)
        binding.introVP.swipeEnabled = false

        binding.introVP.adapter = introAdapter

        binding.introVP.addOnPageChangeListener(OnPageChangeListener {
            val introEnum = NewUserEnum.entries[it]
            binding.continueBTN.text = getString(introEnum.buttonCTA)
            binding.introToolbar.root.isVisible = introEnum.toolbarVisibility
        })

        binding.continueBTN.setOnClickListener {
            when (introAdapter?.getItemType(binding.introVP.currentItem)) {
                IntroItemType.INFO -> goToNextScreen()
                IntroItemType.WHERE_DO_YOU_LIVE -> handleWhereDoYouLive()
                null -> {}
            }
        }
    }

    private fun handleWhereDoYouLive() {
        if (whereDoYouLiveListener?.getCheckedLocation().isNullOrBlank()) {
            Timber.d("User did not select location")
        } else if (whereDoYouLiveListener?.getCheckedLocation() == SIGN_IN_LOCATION) {
            signInResult.launch(signInIntent(flowType = FlowType.ONBOARDING))
        } else {
            preferenceRepository.isUserLoggedIn = false
            goToNextScreen()
        }
    }

    private fun goToNextScreen() {
        val currentItem = binding.introVP.currentItem
        if (currentItem == introAdapter!!.count - 1) {
            startActivity(termsAndConditionsIntent(flowType = FlowType.ONBOARDING))
        } else {
            binding.introVP.setCurrentItem(currentItem + 1, true)
//            binding.introToolbar.announceBackButton()
        }
    }

    private fun goToPreviousScreen() {
        val currentItem = binding.introVP.currentItem
        binding.introVP.setCurrentItem(currentItem - 1, true)
//        binding.introToolbar.announceBackButton()
    }

    companion object {
        const val SIGN_IN_LOCATION = "England"
    }
}
