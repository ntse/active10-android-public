package com.flipsidegroup.active10.presentation.pacechecker.result

data class PaceCheckerResultContent(
    val title: String,
    val description: String,
    val continueButtonTitle: String,
    val captionText: String,
    val captionIconRes: Int,
    val averageImgRes: Int,
    val cadence: String = "",
    val isError: Boolean = false,
)
