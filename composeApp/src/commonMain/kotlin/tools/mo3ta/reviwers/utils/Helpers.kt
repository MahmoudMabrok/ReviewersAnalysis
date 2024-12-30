package tools.mo3ta.reviwers.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun String.getCoverage(): Float? {
    if (this.contains("No Coverage information")){
        return  100f
    }
    val item = this.split("% Coverage]").firstOrNull() ?: return null

    val start = item.lastIndexOf('[')
    val percentage = item.substring(start +1).trim()

    return  percentage.toFloatOrNull()
}


fun formatDate(date:String): String {
    return date.split("T").firstOrNull() ?: ""
}
fun formatDateAsDateWithDayName(input:String): String {
    val dayName = input.toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name
    val date = input.split("T").firstOrNull() ?: ""
    return "$date $dayName"
}



fun mapHoursToTime(total:Long): String {
    val days = total / 24
    val hours = total % 24
    return "$days day $hours hours"
}


fun getTimeRange(start: String, end:String): Long {
    return end.toInstant().minus(start.toInstant()).inWholeHours
}


fun lastDate(data: List<String>): String {
    return data.minOf { it }
}

fun formatDataToDayTime(date:String): String {
    date.toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    return ""
}


