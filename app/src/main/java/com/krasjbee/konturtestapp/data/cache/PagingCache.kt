package com.krasjbee.konturtestapp.data.cache

import com.krasjbee.konturtestapp.domain.Person

/**
 * Cache which supports getting data by pages
 */
interface PagingCache<Data> {
    val isExpired: Boolean

    suspend fun isCacheEmpty(): Boolean
    suspend fun clear()

    suspend fun addAll(collection: List<Data>)

    suspend fun getPage(pageSize: Int, page: Int): List<Data>

    suspend fun getItem(key: Any): Data

    suspend fun searchItem(
        searchQuery: String, pageSize: Int, page: Int
    ): List<Person>

    /**
     * Provides time of last fetch
     */
    interface FetchTimeProvider {
        var lastFetchTime: Long
    }

}

