package com.flipsidegroup.active10.presentation.nhsuserdetails.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.databinding.ActivityNhsUpdateWebViewBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.NhsUpdateWebViewIntent(): Intent {
    return Intent(this, NhsUpdateWebViewActivity::class.java)
}

class NhsUpdateWebViewActivity : BaseSecureActivity<NhsUpdateWebViewContract.View>(), NhsUpdateWebViewContract.View {

    @Inject
    internal lateinit var presenter: NhsUpdateWebViewPresenter
    override fun getPresenter(): LifecycleAwarePresenter<NhsUpdateWebViewContract.View> = presenter

    private var binding: ActivityNhsUpdateWebViewBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNhsUpdateWebViewBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }

        presenter.handleWebView(binding.webView)
    }

    override fun updateSuccess() {
        setResult(RESULT_OK)
        finish()
    }

    override fun updateFailure() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

}