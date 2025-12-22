package com.flipsidegroup.active10.presentation.nhsuserdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivityNhsUserDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.DialogCMSButtonModel
import com.flipsidegroup.active10.presentation.dialogs.LoginBottomSheetDialog
import com.flipsidegroup.active10.presentation.nhsuserdetails.webview.NhsUpdateWebViewIntent
import com.flipsidegroup.active10.utils.DetailItemLayout
import com.flipsidegroup.active10.utils.LoginBottomSheetDialogType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setTextHtml
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.getNhsUserDetailsIntent(): Intent {
    return Intent(this, NhsUserDetailsActivity::class.java)
}

class NhsUserDetailsActivity : BaseSecureActivity<NhsUserDetailsView>(), NhsUserDetailsView {

    @Inject
    internal lateinit var presenter: NhsUserDetailsPresenter

    private var screenContent: ScreenContent? = null

    override fun getPresenter(): LifecycleAwarePresenter<NhsUserDetailsView> = presenter

    private var binding: ActivityNhsUserDetailsBinding by lifecycleAwareVariable()

    private val updateActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            presenter.updateData()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNhsUserDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.detailsToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.detailsToolbar.titleTV.text = getString(R.string.settings_my_details)

        setUpdateButton()
        presenter.loadContent()
        presenter.loadData()
    }

    private fun setUpdateButton() {
        binding.updateBTN.setOnClickListener {
            presenter.getUpdateDialogContent()
        }
    }

    override fun showContent(content: ScreenContent) {
        binding.userDetailsTitleTV.text = content.title
        binding.userDetailsDescriptionTV.setTextHtml(content.description)
        screenContent = content

        contentItemBuilder(binding.nameLayout, "details_name")
        contentItemBuilder(binding.ageLayout, "details_age")
        contentItemBuilder(binding.emailLayout, "details_email")
    }

    private fun contentItemBuilder(view: DetailItemLayout, key: String) {
        val list = screenContent?.getPropertyValue(key)?.split("\r\n")
        list?.let {
            if (list.size == 2) {
                view.setTitle(list[0])
                view.setLabel(list[1])
            }
        }
    }

    override fun showData(nhsUser: NhsUserDetails?) {
        nhsUser?.firstName?.let { binding.nameLayout.setValue(it) }
        nhsUser?.age?.let { binding.ageLayout.setValue(it.toString()) }
        nhsUser?.email?.let { binding.emailLayout.setEmail(it) }
    }

    override fun onUpdateDialogContentReceived(content: ScreenContent) {
        LoginBottomSheetDialog(
            dialogType = LoginBottomSheetDialogType.GREEN_BUTTON,
            title = content.title,
            subtitle = content.description,
            primaryButton = DialogCMSButtonModel(text = content.firstButtonTitle, onClick = {
                updateActivityLauncher.launch(NhsUpdateWebViewIntent())
            }),
            secondaryButton = DialogCMSButtonModel(text = content.secondButtonTitle, onClick = {
                finish()
            })
        ).show(
            supportFragmentManager,
            null
        )
    }
}