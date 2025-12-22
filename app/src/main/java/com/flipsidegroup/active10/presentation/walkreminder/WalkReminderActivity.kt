package com.flipsidegroup.active10.presentation.walkreminder

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TimePicker
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.databinding.ActivityWalkReminderBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.walkreminder.alarmreceiver.AlarmReceiver
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import java.util.Calendar
import java.util.Date

const val IN_IS_FROM_SETTINGS = "IN_IS_FROM_SETTINGS"

fun Context.WalkReminderIntent(isFromSettings: Boolean = false): Intent {
    return Intent(this, WalkReminderActivity::class.java).apply {
        putExtra(IN_IS_FROM_SETTINGS, isFromSettings)
    }
}

class WalkReminderActivity : BaseSecureActivity<BaseView>(), TimePickerDialog.OnTimeSetListener,
    View.OnClickListener {

    private lateinit var timePickerDialog: TimePickerDialog
    private var calendar = Calendar.getInstance()
    private var newReminderTime: Long? = null

    private var binding: ActivityWalkReminderBinding by lifecycleAwareVariable()

    override fun getPresenter(): BasePresenter<BaseView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityWalkReminderBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpToolbar()
        setUpViews()
    }

    private fun setUpToolbar() {
        binding.walkReminderToolbar.titleTV.text = getString(R.string.walk_reminder_title)
        binding.walkReminderToolbar.backTV.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                goBack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setCalendarTime(hourOfDay, minute)

        val timeInMillis = calendar.time.time
        setTime(timeInMillis)

        val isBriskReminderSet = settingsUtils.getSettingsHolder().isBriskReminderSet
        if (isBriskReminderSet != null && isBriskReminderSet) {
            AlarmReceiver.createAlarm(this, timeInMillis)
        }
    }

    private fun setUpViews() {
        val formattedTime = settingsUtils.getSettingsHolder().briskReminderTimestamp

        if (formattedTime == null) {
            val timeInMillis = calendar.time.time
            setTime(timeInMillis)
        } else {
            calendar.time = Date(formattedTime)
            setTime(formattedTime)
        }

        timePickerDialog = TimePickerDialog(
            this,
            R.style.TimePickerTheme,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateHelper.is24HourFormat()
        )

        binding.hourCV.setOnClickListener(this)
        binding.minuteCV.setOnClickListener(this)
        binding.saveBTN.setOnClickListener {
            saveTime(newReminderTime)
            onBackPressed()
        }
    }

    private fun setCalendarTime(hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setTime(timeInMillis: Long) {
        newReminderTime = timeInMillis
        if (!DateHelper.is24HourFormat()) {
            binding.amPmTV.visibility = View.VISIBLE
            binding.amPmTV.text = DateHelper.getAmPm(timeInMillis)
        }

        binding.hourTV.text = DateHelper.getBriskReminderHour(timeInMillis)
        binding.minuteTV.text = DateHelper.getBriskReminderMinute(timeInMillis)
        binding.hourTV.contentDescription = getString(
            R.string.change_hour_time_label,
            binding.hourTV.text.toString(),
            binding.amPmTV.text
        )
        binding.minuteTV.contentDescription = getString(
            R.string.change_minute_time_label,
            binding.minuteTV.text.toString()
        )
    }

    private fun saveTime(timestamp: Long?) {
        timestamp?.let {
            settingsUtils.updateSettings(SettingsDataHolder(briskReminderTimestamp = it))
        }
    }

    private fun getBackStringRes(): Int {
        val isFromSettings = intent.getBooleanExtra(
            com.flipsidegroup.active10.presentation.goals.activities.IN_IS_FROM_SETTINGS,
            false
        )

        return if (isFromSettings) R.string.settings
        else R.string.back
    }
}
