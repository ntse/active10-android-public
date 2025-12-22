package com.flipsidegroup.active10.presentation.fulscreenphoto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityFullscreenPhotoBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walksneardetails.dialog.GoJauntlyDialog
import com.flipsidegroup.active10.utils.loadFromUrl
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber

const val PARAM_URL = "param_url"
const val PARAM_ACTION_TYPE = "param_action"
const val PARAM_ACTION_TEXT = "param_action_text"
const val PARAM_WALK_URL = "param_walk_url"
const val PARAM_WALK_ID = "param_walk_id"
const val PARAM_WALK_TITLE = "param_walk_title"

fun Context.FullScreenPhotoActivityIntent(url: String, actionType: String? = null, actionText: String? = null, actionUrl: String? = null, walkId: String? = null, walkTitle: String? = null): Intent {
    return Intent(this, FullScreenPhotoActivity::class.java).apply {
        putExtra(PARAM_URL, url)
        actionType?.let { putExtra(PARAM_ACTION_TYPE, actionType) }
        actionText?.let { putExtra(PARAM_ACTION_TEXT, actionText) }
        actionUrl?.let { putExtra(PARAM_WALK_URL, actionUrl) }
        walkId?.let { putExtra(PARAM_WALK_ID, walkId) }
        walkTitle?.let { putExtra(PARAM_WALK_TITLE, walkId) }
    }
}

class FullScreenPhotoActivity : BaseSecureActivity<BaseView>() {
    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null


    private lateinit var binding: ActivityFullscreenPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.walks_near_me_title)

        val url = intent.getStringExtra(PARAM_URL)
        binding.photo.loadFromUrl(url)

        val actionType = intent.getStringExtra(PARAM_ACTION_TYPE)
        val actionText = intent.getStringExtra(PARAM_ACTION_TEXT)
        val walkUrl = intent.getStringExtra(PARAM_WALK_URL)
        val walkId = intent.getStringExtra(PARAM_WALK_ID)
        val walkTitle = intent.getStringExtra(PARAM_WALK_TITLE)
        val showAction = actionType != null && actionText != null
        binding.actionButton.isVisible = showAction
        if (showAction) {
            binding.actionButton.text = actionText
            binding.actionButton.setOnClickListener {
                when (actionType) {
                    "OPEN_GO_JAUNTLY" -> {
                        firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                            ctaName = "StartWalkInGoJauntly",
                            walkName = walkTitle ?: ""
                        )
                        GoJauntlyDialog(walkId, walkUrl).show(supportFragmentManager, GoJauntlyDialog::class.java.simpleName)
                    }
                    else -> {
                        Timber.w("Unknown action type: $actionType")
                    }
                }
            }
        }
    }

}