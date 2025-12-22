package com.flipsidegroup.active10.utils

import androidx.core.text.HtmlCompat

fun String.getYoutubeVideoId(): String? {
    val youtubeVideIdPattern =
        "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*".toPattern()
    val matcher = youtubeVideIdPattern.matcher(this)

    return if (matcher.find()) {
        matcher.group()
    } else null
}

fun String.removeHtmlTags(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}


fun String.extractAnchorTag(): String {
    val regex = Regex("<a[^>]*>.*?</a>")
    val matchResult = regex.find(this)
    return matchResult?.value ?: ""
}

fun String.removeAnchorTag(): String {
    val regex = Regex("<a[^>]*>.*?</a>")
    return this.replace(regex, "")
}
