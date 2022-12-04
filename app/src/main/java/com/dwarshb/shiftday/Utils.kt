package com.dwarshb.shiftday

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("MMM dd E")
        val timeFormat: String = "hh:mm a"

        fun formatDateToWeek(inputDate : String,
                             inputDateFormat : String,
                             outputDateFormat: String): String {
            val inputFormat = SimpleDateFormat(inputDateFormat, Locale.ENGLISH)
            val outputFormat = SimpleDateFormat(outputDateFormat, Locale.ENGLISH)

            var date: Date? = null
            var strDate : String? = null

            try {
                date = inputFormat.parse(inputDate) // it's format should be same as inputPattern
                strDate = outputFormat.format(date)
                return strDate
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return inputDate
        }
    }
}