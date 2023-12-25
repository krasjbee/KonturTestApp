package com.krasjbee.konturtestapp.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krasjbee.konturtestapp.datasource.database.typeconverters.InstantConverter

private const val DATABASE_NAME = "person.dp"

@Database(entities = [PersonLocal::class], version = 1, exportSchema = false)
@TypeConverters(
    InstantConverter::class,
)
abstract class PersonDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        @JvmStatic
        fun getInstance(context : Context) =
            Room.databaseBuilder(context, PersonDatabase::class.java, DATABASE_NAME).build()
    }
}