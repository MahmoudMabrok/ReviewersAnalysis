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
    try {
        println(date.toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name)
    }catch (e:Exception){}
    return date.split("T").firstOrNull() ?: ""
}

fun lastDate(data: List<String>): String {
    return data.minOf { it }
}

fun formatDataToDayTime(date:String): String {
    date.toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    return ""
}


