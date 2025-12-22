package com.flipside.briskcounter

import java.util.*


object DateHelper {

    fun isSameDay(firstDate: Date, secondDate: Date): Boolean {
        val firstCalendar = Calendar.getInstance()
        val secondCalendar = Calendar.getInstance()

        firstCalendar.time = firstDate
        secondCalendar.time = secondDate

        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
            && firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR)
    }
}
