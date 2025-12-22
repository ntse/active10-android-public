package com.flipsidegroup.active10.presentation.couch

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivityCouchAdvertBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.openURL
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.CouchAdvertIntent(): Intent {
    return Intent(this, CouchAdvertActivity::class.java)
}

class CouchAdvertActivity : BaseSecureActivity<CouchAdvertView>(), CouchAdvertView {

    @Inject
    internal lateinit var presenter: CouchAdvertPresenter

    override fun getPresenter(): LifecycleAwarePresenter<CouchAdvertView> = presenter

    private var binding: ActivityCouchAdvertBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityCouchAdvertBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }

        presenter.loadContent()
    }

    private fun openCouch() {
        val couchIntent = packageManager.getLaunchIntentForPackage(COUCH_PACKAGE_NAME)
        if (couchIntent != null) {
            startActivity(couchIntent)
            return
        }
        try {
            // Try to open the app page in Google Play app
            openURL("market://details?id=$COUCH_PACKAGE_NAME")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$COUCH_PACKAGE_NAME")))
        } catch (e: ActivityNotFoundException) {
            // If Google Play app is not installed, open in web browser
            openURL("\"https://play.google.com/store/apps/details?id=$COUCH_PACKAGE_NAME\"")
        }
    }

    override fun showContent(content: ScreenContent) {
        with(binding) {
            screen = content
            icon.loadFromUrl(content.firstImageUrl)
            continueButton.setOnClickListener {
                openCouch()
                finish()
            }
            closeButton.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val COUCH_PACKAGE_NAME = "com.phe.couchto5K"
    }

}