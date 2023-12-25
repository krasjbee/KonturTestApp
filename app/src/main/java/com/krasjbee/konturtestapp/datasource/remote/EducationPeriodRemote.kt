package com.krasjbee.konturtestapp.datasource.remote


import com.google.gson.annotations.SerializedName
import com.krasjbee.konturtestapp.domain.EducationPerion
import java.time.Instant

data class EducationPeriodRemote(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String
)

fun EducationPeriodRemote.mapToEducationPeriod() =
    EducationPerion(Instant.parse(start), Instant.parse(end))