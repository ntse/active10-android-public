package com.phe.betterhealth.widgets.common

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnNextLayout
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun TextView.setText(@StringRes textRes: Int?) {
    val oldText = text
    val newText = textRes?.let { context.getString(it) }
    if (newText == oldText || newText == null && oldText.isEmpty()) {
        return
    }
    text = newText
}

@BindingAdapter("textHtml")
fun TextView.setHtml(html: String?) {
    updateHtmlText(html)
    movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("textHtmlNoInteraction")
fun TextView.setHtmlNoInteraction(html: String?) {
    updateHtmlText(html)
}

private fun TextView.updateHtmlText(html: String?) {
    text = html
        ?.replace("<strong>", "<font color=\"black\"><strong>")
        ?.replace("</strong>", "</strong></font>")
        ?.replace("\r\n", "<br/>")
        ?.let {
            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
}

@BindingAdapter("drawableTint")
fun TextView.setDrawableTint(@ColorInt color: Int) {
    val tintList = ColorStateList.valueOf(color)
    TextViewCompat.setCompoundDrawableTintList(this, tintList)
}


@BindingAdapter("iconEnd")
fun TextView.setIconEnd(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}


@BindingAdapter("dynamicEllipsize")
fun TextView.setDynamicEllipsize(auto: Boolean) {
    if (!auto) {
        ellipsize = null
        maxLines = Int.MAX_VALUE
    } else doOnNextLayout {
        ellipsize = TextUtils.TruncateAt.END
        maxLines = height / lineHeight
    }
}

@BindingAdapter("textColor")
fun TextView.textColor(@ColorInt color: Int) {
    setTextColor(color)
}
