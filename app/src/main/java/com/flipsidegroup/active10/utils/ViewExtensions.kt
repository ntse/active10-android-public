package com.flipsidegroup.active10.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context.ACCESSIBILITY_SERVICE
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_YES
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.text.HtmlCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.databinding.BindingAdapter
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.UIUtils.getString
import com.google.android.material.tabs.TabLayout


const val SAMSUNG_ACCESSIBILITY_NAME = "samsung"
fun View.setHeading() {
    var isSamsung = false
    val am: AccessibilityManager? =
        context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager?
    if (am != null && am.isEnabled) {
        val serviceInfoList =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        serviceInfoList?.forEach { serviceInfo ->
            val name = serviceInfo.resolveInfo.serviceInfo.processName
            if (name.contains(SAMSUNG_ACCESSIBILITY_NAME)) {
                isSamsung = true
            }
        }
    }

    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P && !isSamsung) {
        isAccessibilityHeading = true
    } else {
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.roleDescription = getString(R.string.heading)
            }
        })
    }
}

fun View.announceHeader() {
    requestFocus()
    setHeading()
    postDelayed({
        isFocusable = true
        isFocusableInTouchMode = true
        performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
    }, 300)
}

fun ViewGroup.announceBackButton() {
    postDelayed({
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ImageButton) {
                if (hasFocus()) break
                child.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                break
            }
        }
    }, 300)
}

fun View.accessibilityFocusRequest(delayMillis: Long = 600L) {
    postDelayed({
        if (!hasFocus()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    }, delayMillis)
}

fun View.setRoleDescription(role: String) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.roleDescription = role
        }
    })
}

fun View.setAccessibilityButton() {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val btnAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                AccessibilityNodeInfo.ACTION_CLICK,
                getString(R.string.accessibility_click_button)
            )
            info.addAction(btnAction)
            info.isClickable = true
        }
    })
}


fun View.setAccessibilityTab() {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.roleDescription = getString(R.string.accessibility_tab)
            val tabAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                AccessibilityNodeInfo.ACTION_CLICK,
                getString(R.string.accessibility_select_tab)
            )
            info.addAction(tabAction)
        }
    })
}

@BindingAdapter("textHtml")
fun TextView.setTextHtml(html: String?) {
    html?.let {
        val converted = it
            .replace("<strong>", "<font color=\"black\"><strong>")
            .replace("</strong>", "</strong></font>")
            .replace("\r\n", "<br/>")
        text = HtmlCompat.fromHtml(converted, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}

fun TextView.setTextWithUnderlineSpan(highlightText: String, fullText: String, color: Int) {
    val spannableText = SpannableString(fullText)
    val highlightTextStartIndex = fullText.indexOf(highlightText)

    if (highlightTextStartIndex == -1) {
        return
    }

    val highlightTextEndIndex = highlightTextStartIndex + highlightText.length

    spannableText.setSpan(
        UnderlineSpan(),
        highlightTextStartIndex,
        highlightTextEndIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannableText.setSpan(
        ForegroundColorSpan(color),
        highlightTextStartIndex,
        highlightTextEndIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    text = spannableText
}

fun TextView.handleTextLink(string: String, action: (String) -> Unit) {
    text = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val result = SpannableString(text)
    val spans = result.getSpans(0, result.length, URLSpan::class.java)
    for (span in spans) {
        val link:Pair<String, View.OnClickListener> = Pair(span.url, View.OnClickListener {
            action(span.url)
        })
        val start = result.getSpanStart(span)
        val end = result.getSpanEnd(span)
        val flags = result.getSpanFlags(span)
        result.removeSpan(span)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                textView.invalidate()
                link.second.onClick(textView)
            }
        }
        result.setSpan(clickableSpan, start, end, flags)
        highlightColor = Color.TRANSPARENT
        movementMethod = LinkMovementMethod.getInstance()
        setText(result, TextView.BufferType.SPANNABLE)
    }
}

fun ImageView.loadGif(
    @DrawableRes gif: Int?,
    @DrawableRes placeholderResId: Int?,
    onGifFinished: (() -> Unit)? = null
) {

    if (placeholderResId == null) {
        onGifFinished?.invoke()
        return
    }
    if (gif == null) {
        setImageResource(placeholderResId)
        onGifFinished?.invoke()
        return
    }

    val requestOptions = RequestOptions()
        .placeholder(placeholderResId)
        .error(placeholderResId)

    val glideRequest = Glide.with(this.context)
        .asGif()
        .load(gif)
        .apply(requestOptions)
        .skipMemoryCache(true)

    onGifFinished?.let {
        glideRequest.listener(object : RequestListener<GifDrawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>,
                isFirstResource: Boolean
            ): Boolean {
                it.invoke()
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable,
                model: Any,
                target: Target<GifDrawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                resource.setLoopCount(1)
                resource.registerAnimationCallback(object :
                    Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        it.invoke()
                    }
                })
                return false
            }
        })
    }
    glideRequest.into(this)
}

fun ImageView.loadFromUrl(
    url: String?,
    @DrawableRes placeholderResId: Int? = 0,
    onLoadFinished: (() -> (Unit))? = null,
    isTransition: Boolean = false
) {
    if (placeholderResId == null) {
        onLoadFinished?.invoke()
        return
    }
    if (url == null) {
        setImageResource(placeholderResId)
        onLoadFinished?.invoke()
        return
    }

    val strategy = if (isTransition) DiskCacheStrategy.NONE
    else DiskCacheStrategy.ALL

    val requestOptions = RequestOptions()
        .placeholder(placeholderResId)
        .error(placeholderResId)
        .diskCacheStrategy(strategy)
        .skipMemoryCache(isTransition)

    val glideRequest = if (isTransition) {
        Glide.with(this.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .apply(requestOptions)
    } else {
        Glide.with(this.context)
            .load(url)
            .apply(requestOptions)
    }

    onLoadFinished?.let {
        glideRequest.listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                it.invoke()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                it.invoke()
                return false
            }
        })
    }
    glideRequest.into(this)
}

fun View.setIsVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun ViewGroup.disableAllButtons() {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (child is Button) {
            child.isClickable = false
        } else if (child is ViewGroup) {
            child.disableAllButtons()
        }
    }
}

fun ViewGroup.enableAllButtons() {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (child is Button) {
            child.isClickable = true
        } else if (child is ViewGroup) {
            child.enableAllButtons()
        }
    }
}

fun ViewGroup.blockAllButtonsInView(debounceTime: Long) {
    disableAllButtons()
    Handler(Looper.getMainLooper()).postDelayed({
        enableAllButtons()
    }, debounceTime)
}

fun View.setOnClickListenerWithDebounce(debounceTime: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
            (v.rootView as ViewGroup).blockAllButtonsInView(debounceTime)
        }
    })
}

fun LottieAnimationView.playAnimationIfEnabled(isAnimationEnabled: Boolean) {
    if (isAnimationEnabled) {
        this.playAnimation()
    } else {
        this.progress = 1f
    }
}

fun TabLayout.onTabSelected(callback: (TabLayout.Tab) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { callback(it) }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}

fun View.getStatusBarHeight(): Int {
    val statusBar = Rect()
    rootView.getWindowVisibleDisplayFrame(statusBar)
    return statusBar.top
}

fun View.getYScreenPosition(): Int {
    val point = IntArray(2)
    getLocationOnScreen(point)
    val (_, y) = point
    return y
}
