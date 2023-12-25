package com.krasjbee.konturtestapp.ui.entities

import androidx.compose.runtime.Stable
import com.krasjbee.konturtestapp.domain.Person

@Stable
data class PersonUI(
    val id: String,
    val name: String,
    val phone: String,
    val height: String,
    val biography: String,
    val temperament: String,
    val educationPeriodUi: EducationPeriodUI
)

fun Person.mapToUi(): PersonUI =
    PersonUI(id, name, phone, height.toString(), biography, temperament, educationPeriod.mapToUi())
