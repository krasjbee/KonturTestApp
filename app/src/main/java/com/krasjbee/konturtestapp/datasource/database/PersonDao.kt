package com.krasjbee.konturtestapp.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao {

    @Query("SELECT * FROM person LIMIT :pageSize OFFSET :page*:pageSize")
    suspend fun getPersonList(pageSize: Int, page: Int): List<PersonLocal>

    @Query("SELECT * FROM person WHERE name LIKE '%'|| :searchQuery  ||'%' OR phone LIKE '%'|| :searchQuery ||'%' LIMIT :pageSize OFFSET :page*:pageSize")
    suspend fun searchPersons(searchQuery: String, pageSize: Int, page: Int): List<PersonLocal>

    @Query("DELETE FROM PERSON")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PersonLocal>)

    @Query("SELECT * FROM person WHERE id = :personId")
    suspend fun getPersonInfo(personId: String): PersonLocal

    //in case of cache is invalidated it's faster to check if there is any amount of record instead of counting all the data
    @Query("SELECT count(*) FROM (SELECT * FROM person LIMIT 1)")
    suspend fun checkIsEmpty(): Int
}