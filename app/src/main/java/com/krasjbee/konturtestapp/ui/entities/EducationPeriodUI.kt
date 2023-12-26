package com.krasjbee.konturtestapp.ui.entities

import androidx.compose.runtime.Stable
import com.krasjbee.konturtestapp.domain.EducationPeriod

@Stable
data class EducationPeriodUI(
    val start: String,
    val end: String
) {
    val formattedPeriod = "$start - $end"
}

fun EducationPeriod.mapToUi() : EducationPeriodUI =
    EducationPeriodUI(start = "Stub start",
//    DateTimeFormatter.ISO_DATE.format(start),
        end = "Stub end"
//        DateTimeFormatter.ISO_DATE.format(end)
    ) // TODO: fix
