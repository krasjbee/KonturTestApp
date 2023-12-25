package com.krasjbee.konturtestapp.datasource.database.typeconverters

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilli()
}