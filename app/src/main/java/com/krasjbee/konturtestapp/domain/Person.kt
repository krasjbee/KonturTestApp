package com.krasjbee.konturtestapp.domain

data class Person(
    val id: String,
    val name: String,
    val phone: String,
    val height: Double,
    val biography: String,
    val temperament: String,
    val educationPeriod: EducationPeriod
)
