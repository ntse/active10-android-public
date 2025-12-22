package com.flipsidegroup.active10.presentation.licenses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.AcknowledgementLicense
import com.flipsidegroup.active10.databinding.ActivityLicensesBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.openURL
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import javax.inject.Inject

fun Context.LicensesActivity(): Intent {
    return Intent(this, LicensesActivity::class.java)
}

class LicensesActivity: BaseSecureActivity<LicensesContract.View>(), LicensesContract.View {

    @Inject
    internal lateinit var presenter: LicensesContract.Presenter

    private val licensesAdapter = LicensesAdapter()

    override fun getPresenter(): LifecycleAwarePresenter<LicensesContract.View> = presenter

    private var binding: ActivityLicensesBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityLicensesBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.licensesToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.licensesToolbar.titleTV.text = getString(R.string.acknowledgements_title)

        setUpRecyclerView()

        presenter.getLicenses()
    }

    private fun setUpRecyclerView() {
        binding.licensesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.licensesRecyclerView.adapter = licensesAdapter
        licensesAdapter.onItemClickListener = { license ->
            Timber.d("License clicked: $license")
            license.url?.let { openURL(it) }
        }
    }

    override fun showLicenses(licenses: List<AcknowledgementLicense>) {
        binding.errorMessage.isVisible = false
        licensesAdapter.submitList(licenses)
    }

    override fun showError() {
        Timber.e("Error while getting licenses")
        binding.errorMessage.text = "Error while getting licenses"
        binding.errorMessage.isVisible = true
    }

}