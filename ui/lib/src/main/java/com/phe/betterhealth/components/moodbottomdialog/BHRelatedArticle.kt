package com.phe.betterhealth.components.moodbottomdialog

import java.io.Serializable

data class BHRelatedArticle(
    val categoryLabel: String,
    val title: String,
    val imageUrl: String,
    val isLink: Boolean,
): Serializable
