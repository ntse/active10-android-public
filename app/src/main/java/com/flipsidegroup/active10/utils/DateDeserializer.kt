package com.flipsidegroup.active10.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import timber.log.Timber
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date?> {

    companion object {
        private const val TWELVE_HOUR_MODE_FORMAT = "MMM dd, yyyy h:mm:ss a"
        private const val TWENTY_FOUR_HOUR_MODE_FORMAT = "MMM dd, yyyy hh:mm:ss"
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        arg1: Type?,
        arg2: JsonDeserializationContext?
    ): Date? {
        val date = element.asString

        // Try 12 hour
        try {
            val formatter =
                SimpleDateFormat(TWELVE_HOUR_MODE_FORMAT, Locale.UK)
            return formatter.parse(date)
        } catch (e: ParseException) {
            Timber.d("Cannot deserialize TWELVE_HOUR_MODE_FORMAT")
        }

        // Try 24 hour
        try {
            val formatter = SimpleDateFormat(TWENTY_FOUR_HOUR_MODE_FORMAT, Locale.UK)
            return formatter.parse(date)
        } catch (e: ParseException) {
            Timber.d("Cannot deserialize TWENTY_FOUR_HOUR_MODE_FORMAT")
        }
        return null
    }
}
