package com.flipsidegroup.active10.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import com.flipsidegroup.active10.Active10App
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


private const val TWENTY_FOUR_HOUR_TIME_FORMAT = "HH:mm"
private const val HH_MM_FORMAT = "HH mm"
private const val TWELVE_HOUR_TIME_FORMAT = "hh:mm a"
private const val TWENTY_FOUR_HOUR_FORMAT = "HH"
private const val ANALYTICS_DATE_FORMAT = "yyyy-MM-dd"
private const val TWELVE_HOUR_FORMAT = "hh"
private const val MINUTE_FORMAT = "mm"
private const val AM_PM_FORMAT = "a"
private const val MONTH_FORMAT = "MMMM"
private const val BADGE_DATE_FORMAT = "dd/MM/yyyy"
private const val DAY_MONTH_DATE_FORMAT = "dd MMMM"
private const val ANALYTICS_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"


object DateHelper {

    fun getBriskReminderHour(timestamp: Long): String {
        val dateFormat = if (is24HourFormat()) TWENTY_FOUR_HOUR_FORMAT else TWELVE_HOUR_FORMAT
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(timestamp)
    }

    fun getPrefixTime(timestamp: Long): String {
        return SimpleDateFormat(HH_MM_FORMAT, Locale.getDefault()).format(timestamp)
    }

    fun getAmPm(timestamp: Long): String {
        return SimpleDateFormat(AM_PM_FORMAT, Locale.getDefault()).format(timestamp)
    }

    fun getReminderTime(timestamp: Long): String {
        val dateFormat =
            if (is24HourFormat()) TWENTY_FOUR_HOUR_TIME_FORMAT else TWELVE_HOUR_TIME_FORMAT
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(timestamp)
    }

    fun getBriskReminderMinute(timestamp: Long): String {
        return SimpleDateFormat(MINUTE_FORMAT, Locale.getDefault()).format(timestamp)
    }

    fun getUpdatedAtTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
    }

    fun is24HourFormat(): Boolean {
        return DateFormat.is24HourFormat(Active10App.instance.applicationContext)
    }

    fun analyticsDateTimeFormat(timestamp: Long): String {
        return SimpleDateFormat(ANALYTICS_DATE_TIME_FORMAT, Locale.getDefault()).format(timestamp)
    }

    fun getDayOfWeekFullName(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            ?: ""
    }

    fun getDayOfWeek(cal: Calendar): String =
        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

    fun getMonthName(cal: Calendar): String =
        cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""

    fun getMonthFullName(cal: Calendar): String =
        cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""

    fun getMonthWithYearName(cal: Calendar): String =
        getMonthFullName(cal).plus(" ").plus(cal.get(Calendar.YEAR))

    fun isSameDay(oldCalendar: Calendar): Boolean {
        val newCalendar = Calendar.getInstance()

        return oldCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR) &&
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
    }

    fun isSameOrPreviousDate(oldCalendar: Calendar, newCalendar: Calendar): Boolean {
        return oldCalendar.get(Calendar.YEAR) < newCalendar.get(Calendar.YEAR) ||
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                oldCalendar.get(Calendar.DAY_OF_YEAR) < newCalendar.get(Calendar.DAY_OF_YEAR) ||
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                oldCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameDay(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        return oldCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR) &&
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
    }

    fun getStartOfCurrentWeek(): Calendar {
        val start = Calendar.getInstance()
        if (start.firstDayOfWeek == Calendar.SUNDAY &&
            start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            start.add(Calendar.WEEK_OF_MONTH, -1)
        }
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return getStartOfDay(start)
    }

    fun getEndOfCurrentWeek(): Calendar {
        val end = Calendar.getInstance()
        if (end.firstDayOfWeek == Calendar.SUNDAY &&
            end.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
        ) {
            end.add(Calendar.WEEK_OF_MONTH, 1)
        }
        end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return getEndOfDay(end)
    }

    fun getStartOfCurrentMonth(): Calendar {
        val start = Calendar.getInstance()
        start.set(Calendar.DAY_OF_MONTH, start.getActualMinimum(Calendar.DATE))
        return getStartOfDay(start)
    }

    fun getEndOfCurrentMonth(): Calendar {
        val end = Calendar.getInstance()
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DATE))
        return getEndOfDay(end)
    }

    fun getStartOfDay2WeeksAgo(): Date {
        return Calendar
            .getInstance()
            .apply { time = LocalDate.now().minusWeeks(2).toDate() }
            .let { getStartOfDay(it) }
            .time
    }

    fun getEndOfYesterday(): Date {
        return Calendar
            .getInstance()
            .apply { time = LocalDate.now().minusDays(1).toDate() }
            .let { getEndOfDay(it) }
            .time
    }


    fun getTimeTillMidnight(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return (calendar.timeInMillis - System.currentTimeMillis())
    }

    fun formatStepDataTimestamp(timestamp: Long): String {
        return ISODateTimeFormat.dateTimeNoMillis().print(DateTime(timestamp))
    }

    fun getStartOfDay(date: Calendar): Calendar {
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)
        return date
    }

    fun getEndOfDay(date: Calendar): Calendar {
        date.set(Calendar.HOUR_OF_DAY, 23)
        date.set(Calendar.MINUTE, 59)
        date.set(Calendar.SECOND, 59)
        date.set(Calendar.MILLISECOND, 999)
        return date
    }

    fun isSameWeek(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        if (newCalendar.firstDayOfWeek == Calendar.SUNDAY &&
            newCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            newCalendar.add(Calendar.WEEK_OF_MONTH, -1)
        }

        return newCalendar.get(Calendar.WEEK_OF_YEAR) == oldCalendar.get(Calendar.WEEK_OF_YEAR) &&
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
    }

    fun isLastWeek(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        oldCalendar.add(Calendar.WEEK_OF_MONTH, 1)
        if (newCalendar.firstDayOfWeek == Calendar.SUNDAY &&
            newCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            newCalendar.add(Calendar.WEEK_OF_MONTH, -1)
        }

        return newCalendar.get(Calendar.WEEK_OF_YEAR) == oldCalendar.get(Calendar.WEEK_OF_YEAR) &&
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
    }

    fun isSameMonth(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        return oldCalendar.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH) &&
                oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)
    }

    fun isOlderThan30Days(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        newCalendar.add(Calendar.DAY_OF_YEAR, -30)
        return oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                oldCalendar.get(Calendar.DAY_OF_YEAR) < newCalendar.get(Calendar.DAY_OF_YEAR) ||
                getDateDiffInDays(timestamp) > 30
    }

    fun isOlderThan10Minute(timestamp: Long): Boolean {
        val newCalendar = Calendar.getInstance()
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = timestamp
        newCalendar.add(Calendar.MINUTE, -10)
        return oldCalendar.timeInMillis < newCalendar.timeInMillis
    }

    fun getCurrentTimestamp(): Long {
        val currentDate = Date()
        return currentDate.time
    }

    fun getDateDiffInMinutes(timestamp: Long?): Long {
        if (timestamp == null) {
            return 0
        }

        val diff = timestamp - getCurrentTimestamp()
        return diff / MINUTE_IN_MILLIS
    }

    fun getDateDiffInDays(timestamp: Long?): Long {
        if (timestamp == null) {
            return 0
        }

        val diff = getCurrentTimestamp() - timestamp
        return diff / DAY_IN_MILLIS
    }

    fun getDateDiffInDays(start: Long?, end: Long?): Long {
        if (start == null || end == null) {
            return 0
        }

        val startDate = getStartTimestampOfDay(start)
        val endDate = getStartTimestampOfDay(end)
        val diff = endDate - startDate
        return round(diff.toDouble() / DAY_IN_MILLIS).toLong()
    }

    fun getStartTimestampOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return getStartOfDay(calendar).timeInMillis
    }

    fun getEndTimestampOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return getEndOfDay(calendar).timeInMillis
    }

    fun getInstalledDate(context: Context?): Calendar {
        val installed =
            context?.packageManager?.getPackageInfo(context.packageName, 0)?.firstInstallTime
        val installedDate = Calendar.getInstance()
        if (installed != null) {
            installedDate.time = Date(installed)
        }
        return getStartOfDay(installedDate)
    }

    fun formatAnalyticsDate(timestamp: Long): String {
        return SimpleDateFormat(ANALYTICS_DATE_FORMAT, Locale("en_GB")).format(Date(timestamp))
    }

    fun formatBadgeDate(timestamp: Long?): String {
        return SimpleDateFormat(BADGE_DATE_FORMAT, Locale.getDefault()).format(Date(timestamp ?: System.currentTimeMillis()))
    }

    fun parseStringDate(stringDate: String): Long {
        return SimpleDateFormat(ANALYTICS_DATE_FORMAT, Locale.getDefault()).parse(stringDate)?.time
            ?: 0
    }

    fun isSameDay(firstDate: Long, secondDate: Long): Boolean {
        val firstCalendar = Calendar.getInstance()
        val secondCalendar = Calendar.getInstance()

        firstCalendar.timeInMillis = firstDate
        secondCalendar.timeInMillis = secondDate

        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
                && firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR)
    }

    fun getMonth(timestamp: Long): String {
        return SimpleDateFormat(MONTH_FORMAT, Locale.getDefault()).format(timestamp)
    }

    fun isStartOfWeek(timestamp: Long?): Boolean {
        if (timestamp == null) {
            return false
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.firstDayOfWeek == Calendar.SUNDAY && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
    }

    fun weekNumber(timestamp: Long?): Int {
        if (timestamp == null) {
            return 0
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun formatDayMonth(timestamp: Long): String {
        return SimpleDateFormat(DAY_MONTH_DATE_FORMAT, Locale.getDefault()).format(Date(timestamp))
    }

    fun getNextHourOccurrence(timestamp: Long, currentTimeStamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        calendar.timeInMillis = currentTimeStamp

        if (hour > calendar.get(Calendar.HOUR_OF_DAY) || (hour == calendar.get(Calendar.HOUR_OF_DAY) && minute > calendar.get(Calendar.MINUTE))) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
        }

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }
}
