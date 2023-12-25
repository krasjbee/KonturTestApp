package com.krasjbee.konturtestapp.domain

import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    suspend fun getPersonList(force: Boolean = false, pageSize: Int, page : Int): Result<List<Person>>

    suspend fun getPerson(personId: String): Result<Person>

    suspend fun searchPersons(searchQuery: String,pageSize: Int, page : Int): Result<List<Person>>

}