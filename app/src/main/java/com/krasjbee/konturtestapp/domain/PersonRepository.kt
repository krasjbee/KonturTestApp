package com.krasjbee.konturtestapp.domain

import kotlinx.coroutines.flow.Flow

interface PersonRepository {

    fun getPersonList(force: Boolean = false, pageSize: Int, page : Int): Flow<Result<List<Person>>>

    fun getPerson(personId: String): Flow<Result<Person>>

    fun searchPersons(searchQuery: String): Flow<Result<List<Person>>>

}