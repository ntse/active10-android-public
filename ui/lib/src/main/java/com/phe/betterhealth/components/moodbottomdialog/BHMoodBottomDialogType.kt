package com.phe.betterhealth.components.moodbottomdialog

import java.io.Serializable

data class MoodBottomDialogType (
    val type: BHMoodBottomDialogType
) : Serializable

enum class BHMoodBottomDialogType(val id: Int) {
    GREAT(0),
    GOOD(1),
    OK(2),
    TIRED(3);
}
