package com.krasjbee.konturtestapp.domain

interface PersonRepository {
    suspend fun getPersonList(
        force: Boolean = false,
        pageSize: Int,
        page: Int
    ): DataHolder<List<Person>>

    suspend fun getPerson(personId: String): DataHolder<Person>

    suspend fun searchPersons(
        force: Boolean = false,
        searchQuery: String,
        pageSize: Int,
        page: Int
    ): DataHolder<List<Person>>
}