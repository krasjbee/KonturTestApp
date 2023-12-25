package com.krasjbee.konturtestapp.datasource.remote


import com.google.gson.annotations.SerializedName
import com.krasjbee.konturtestapp.domain.EducationPeriod
import java.time.Instant
import java.time.format.DateTimeFormatter

data class EducationPeriodRemote(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String
)

fun EducationPeriodRemote.mapToEducationPeriod() =
    EducationPeriod(Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(start)), Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(end)))