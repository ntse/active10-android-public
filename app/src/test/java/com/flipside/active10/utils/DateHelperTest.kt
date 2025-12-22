package com.flipside.active10.utils

import com.flipsidegroup.active10.utils.DateHelper
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class DateHelperTest {

    @Test
    fun `getNextHourOccurrence with today result`() {
        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = 1698134400000  // 24 Oct 2023 - 10:00
        }
        val timeToFindOccurrence = Calendar.getInstance().apply {
            timeInMillis = 1692698400000  // 22 Oct 2023 - 12:00
        }
        val expectedResult = Calendar.getInstance().apply {
            timeInMillis = 1698141600000  // 24 Oct 2023 - 12:00
        }.timeInMillis

        val resultTimeStamp = DateHelper.getNextHourOccurrence(timeToFindOccurrence.timeInMillis, currentCalendar.timeInMillis)

        Assert.assertEquals(expectedResult, resultTimeStamp)
    }


    @Test
    fun `getNextHourOccurrence with tomorrow result`() {
        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = 1698134400000  // 24 Oct 2023 - 10:00
        }
        val timeToFindOccurrence = Calendar.getInstance().apply {
            timeInMillis = 1697954400000  // 22 Oct 2023 - 8:00
        }
        val expectedResult = Calendar.getInstance().apply {
            timeInMillis = 1698213600000  // 25 Oct 2023 - 8:00
        }.timeInMillis

        val resultTimeStamp = DateHelper.getNextHourOccurrence(timeToFindOccurrence.timeInMillis, currentCalendar.timeInMillis)

        Assert.assertEquals(expectedResult, resultTimeStamp)
    }


    @Test
    fun `getNextHourOccurrence ignore seconds`() {
        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = 1698134400000  // 24 Oct 2023 - 10:00:00
        }
        val timeToFindOccurrence = Calendar.getInstance().apply {
            timeInMillis = 1697954430000  // 22 Oct 2023 - 8:00:30
        }
        val expectedResult = Calendar.getInstance().apply {
            timeInMillis = 1698213600000  // 25 Oct 2023 - 8:00:00
        }.timeInMillis

        val resultTimeStamp = DateHelper.getNextHourOccurrence(timeToFindOccurrence.timeInMillis, currentCalendar.timeInMillis)

        Assert.assertEquals(expectedResult, resultTimeStamp)
    }

}