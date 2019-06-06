package com.iterika.marvel.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    private val today = Calendar.getInstance()
    private val dateTimeFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun format(date: String?): String {
        return getLocalDateTimeString(getCalendar(date))
    }

    fun getCalendar(date: String?) = try {
        Calendar.getInstance().apply {
            time = serverDateFormat.parse(date)
        }
    } catch (e: Exception) {
        Calendar.getInstance()
    }

    private fun getLocalDateTimeString(c: Calendar) = when {
        today.get(Calendar.DATE) == c.get(Calendar.DATE) -> "Сегодня"
        today.get(Calendar.DATE) - c.get(Calendar.DATE) == 1 -> "Вчера"
        else -> dateTimeFormat.format(c.time)
    }
}