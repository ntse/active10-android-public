package com.flipsidegroup.active10.presentation.circularwalk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.databinding.ActivityCircularWalkBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walksneardetails.dialog.GoJauntlyDialog
import com.flipsidegroup.active10.utils.loadFromUrl
import com.google.gson.Gson
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val PARAM_CIRCULAR_WALK_DATA = "param_circular_walk_data"

fun Context.CircularWalkIntent(circularWalkData: CircularWalkResponse): Intent {
    return Intent(this, CircularWalkDetailsActivity::class.java).apply {
        putExtra(
            PARAM_CIRCULAR_WALK_DATA,
            Gson().toJson(circularWalkData)
        )
    }
}

class CircularWalkDetailsActivity : BaseSecureActivity<CircularWalkDetailsView>(), CircularWalkDetailsView {

    @Inject
    lateinit var presenter: CircularWalkDetailsPresenter

    override fun getPresenter(): LifecycleAwarePresenter<CircularWalkDetailsView> = presenter

    private lateinit var binding: ActivityCircularWalkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCircularWalkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.walks_near_me_title)

        presenter.loadContent()
    }

    override fun showContent(content: ScreenContent) {
        val data = intent.getStringExtra(PARAM_CIRCULAR_WALK_DATA)

        if (!data.isNullOrEmpty()) {
            Gson().fromJson(data, CircularWalkResponse::class.java)?.let { walkData ->
                loadData(walkData, content)
            }
        }
    }

    private fun loadData(walkData: CircularWalkResponse, content: ScreenContent) {
        with(binding) {
            photo.loadFromUrl(walkData.thumbnailUrl)
            title.text = walkData.title
            ascend.text = getString(R.string.meters_short, walkData.ascend.toString())
            descend.text = getString(R.string.meters_short, walkData.descend.toString())
            firstButton.text = content.firstButtonTitle
            firstButton.setOnClickListener {
                firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                    ctaName = "FindMoreRoutesInGoJauntly",
                    walkName = walkData.title
                )
                GoJauntlyDialog(null, null).show(supportFragmentManager, GoJauntlyDialog::class.java.simpleName)
            }
            secondButton.text = content.secondButtonTitle
            secondButton.setOnClickListener {
                firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                    ctaName = "StartWalkInGoJauntly",
                    walkName = walkData.title
                )
                GoJauntlyDialog(walkData.uuid, walkData.url).show(supportFragmentManager, GoJauntlyDialog::class.java.simpleName)
            }
        }
    }

}