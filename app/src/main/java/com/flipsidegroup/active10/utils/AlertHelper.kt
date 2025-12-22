package com.flipsidegroup.active10.utils

import android.app.Activity
import android.text.TextUtils
import android.text.format.DateUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.AlerterNotificationBinding
import com.tapadoo.alerter.Alerter

object AlertHelper {

    fun showErrorToast(error: String?, toastLength: Int = Toast.LENGTH_SHORT) {
        if (TextUtils.isEmpty(error)) {
            return
        }

        Toast.makeText(UIUtils.getAppContext(), error, toastLength).show()
    }

    fun showAlerter(activity: Activity, text: String, buttonText: String) {
        Alerter.create(activity, R.layout.alerter_notification)
            .setDuration(DateUtils.MINUTE_IN_MILLIS)
            .setBackgroundColorRes(R.color.white)
            .enableSwipeToDismiss()
            .setDismissable(false)
            .also { alerter ->
                alerter.getLayoutContainer()?.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).also {
                    it.setMargins(
                        UIUtils.convertDpToPx(-16f),
                        UIUtils.convertDpToPx(-16f),
                        UIUtils.convertDpToPx(-16f),
                        UIUtils.convertDpToPx(-16f)
                    )
                }
                val binding = alerter.getLayoutContainer()?.let {
                    AlerterNotificationBinding.bind(it)
                }

                val titleTv = binding?.titleTv
                val button = binding?.dismissBtn
                titleTv?.contentDescription = text
                titleTv?.text = text

                button?.text = buttonText
                button?.setOnClickListener { Alerter.hide() }
            }
            .show()
    }
}