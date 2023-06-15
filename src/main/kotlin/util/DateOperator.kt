package util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.GERMAN)

class DateOperator {
    companion object {
        fun parseDateFromString(target: String): Date? {
            return try {
                inputDateFormat.parse(target)
            } catch (e: ParseException) {
                null
            }
        }
    }

}