package com.krasjbee.konturtestapp.datasource.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.krasjbee.konturtestapp.domain.EducationPerion
import com.krasjbee.konturtestapp.domain.Person
import java.time.Instant

@Entity(tableName = "person")
data class PersonLocal(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val height: Double,
    val biography: String,
    val temperament: String,
    val educationPeriodStart: Instant,
    val educationPeriodEnd: Instant,
)

fun PersonLocal.mapToPerson(): Person = Person(
    id = id,
    phone = phone,
    height = height,
    name = name,
    biography = biography,
    temperament = temperament,
    educationPeriod = EducationPerion(educationPeriodStart, educationPeriodEnd)
)

fun Person.mapToLocal(): PersonLocal = PersonLocal(
    id = id,
    name = name,
    phone = phone,
    height = height,
    biography = biography,
    temperament = temperament,
    educationPeriodStart = educationPeriod.start,
    educationPeriodEnd = educationPeriod.end
)
