package com.krasjbee.konturtestapp.ui.entities

import androidx.compose.runtime.Stable
import com.krasjbee.konturtestapp.domain.EducationPeriod
import com.krasjbee.konturtestapp.util.APP_DATE_FORMATTER
import java.time.ZoneId

@Stable
data class EducationPeriodUI(
    val start: String,
    val end: String
) {
    val formattedPeriod = "$start - $end"
}

fun EducationPeriod.mapToUi(): EducationPeriodUI =
    EducationPeriodUI(
        start = APP_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(start),
        end = APP_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(end)
    )
