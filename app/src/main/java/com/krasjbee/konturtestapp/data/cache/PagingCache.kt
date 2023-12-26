package com.krasjbee.konturtestapp.data.cache

import com.krasjbee.konturtestapp.domain.Person

interface PagingCache<Data> {
    val isExpired: Boolean

    val isEmpty: Boolean
    suspend fun clear()

    //    suspend fun add(dataElement : Data)
//
    suspend fun addAll(collection: List<Data>) // TODO: consider to move to collection

    suspend fun getPage(pageSize: Int, page: Int): List<Data>

    suspend fun getItem(key: Any): Data

    suspend fun searchItem(
        searchQuery: String, pageSize: Int, page: Int
    ): List<Person>

    interface FetchTimeProvider {
        var lastFetchTime: Long
    }

}

