package com.finastra.kvdtechnical.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(dateUTC: String?) : String {
    if (dateUTC.isNullOrEmpty()) return ""

    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateUTC)
    val newFormat = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
    return date?.let { newFormat.format(it) }.orEmpty()
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

fun getCurrentDate() : String {
    val date = Calendar.getInstance().time
    val newFormat = SimpleDateFormat("yyyy, MMMM dd", Locale.getDefault())
    return newFormat.format(date)
}

fun getMarsDate() : String {
    val date = Calendar.getInstance().time
    val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return newFormat.format(date)
}