package com.krasjbee.konturtestapp.util

import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

val APP_DATE_FORMATTER = DateTimeFormatterBuilder()
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral('.')
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('.')
    .appendValue(ChronoField.YEAR)
    .toFormatter()