package com.krasjbee.konturtestapp.datasource.remote


import com.google.gson.annotations.SerializedName
import com.krasjbee.konturtestapp.domain.Person

data class PersonRemote(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("height")
    val height: Double,
    @SerializedName("biography")
    val biography: String,
    @SerializedName("temperament")
    val temperament: String,
    @SerializedName("educationPeriod")
    val educationPeriodRemote: EducationPeriodRemote
)

fun PersonRemote.mapToPerson(): Person = Person(
    id = id,
    name = name,
    phone = phone,
    height = height,
    biography = biography,
    temperament = temperament,
    educationPeriod = educationPeriodRemote.mapToEducationPeriod()
)


/*
{
    "id": "5bbb009d5d052e0b9258c316",
    "name": "Summer Greer",
    "phone": "+7 (903) 425-3032",
    "height": 201.9,
    "biography": "Non culpa occaecat occaecat sit occaecat aliquip esse Lorem voluptate commodo veniam ipsum velit. Mollit sunt quis reprehenderit pariatur Lorem consequat magna. Nulla nostrud ad deserunt tempor proident enim exercitation sit ullamco aliquip.",
    "temperament": "sanguine",
    "educationPeriod": {
      "start": "2013-07-15T11:44:06-06:00",
      "end": "2007-08-09T08:26:05-06:00"
    }
  }
 */