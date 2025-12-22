package com.flipsidegroup.active10.utils

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.R


object UIUtils {

    fun getAppContext(): Context {
        return Active10App.instance.applicationContext
    }

    fun getColor(@ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(getAppContext(), colorResId)
    }

    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
        return getAppContext().getString(stringResId, *formatArgs)
    }

    fun getDimension(@DimenRes dimenResId: Int): Float {
        return getAppContext().resources.getDimension(dimenResId)
    }

    fun getDimensionInPx(@DimenRes dimenResId: Int): Int {
        return getAppContext().resources.getDimensionPixelSize(dimenResId)
    }

    fun getQuantityString(@PluralsRes pluralsResId: Int, quantity: Int?): String {
        return if (quantity == null) {
            ""
        } else {
            getAppContext().resources.getQuantityString(pluralsResId, quantity, quantity)
        }
    }

    fun checkIfFileExists(fileName: String): Boolean {
        val fileList = getAppContext().fileList()
        val match = fileList.firstOrNull { it == fileName }
        return match != null
    }

    fun convertDpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getHorseShoeRewardImage(progress: Int) =
        when (progress) {
            1 -> R.drawable.ic_todayswalks1
            2 -> R.drawable.ic_todayswalks2
            3 -> R.drawable.ic_todayswalks3
            4 -> R.drawable.ic_todayswalks4
            5 -> R.drawable.ic_todayswalks5
            6 -> R.drawable.ic_todayswalks6
            7 -> R.drawable.ic_todayswalks7
            8 -> R.drawable.ic_todayswalks8
            9 -> R.drawable.ic_todayswalks9
            10 -> R.drawable.ic_todayswalks10
            else -> null
        }
}
