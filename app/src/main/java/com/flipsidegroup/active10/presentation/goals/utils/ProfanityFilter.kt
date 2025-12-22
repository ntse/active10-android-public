package com.flipsidegroup.active10.presentation.goals.utils

import android.text.Editable


class ProfanityFilter(
    private var badWordsList: List<String>
) {

    fun filter(
        text: Editable,
        onFiltered: (badWords: List<KotlinVersion>, containsBadWords: Boolean) -> (Unit)
    ) {
        val versions = ArrayList<KotlinVersion>()

        badWordsList.forEach {
            if (text.contains(it)) {
                val startIndexOf = text.indexOf(it)
                versions.add(KotlinVersion(startIndexOf, startIndexOf + it.length))
            }
        }

        onFiltered.invoke(versions, versions.isNotEmpty())
    }
}