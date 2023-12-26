package com.krasjbee.konturtestapp.data

import android.util.Log
import com.krasjbee.konturtestapp.data.cache.PagingCache
import com.krasjbee.konturtestapp.datasource.remote.PersonApiClient
import com.krasjbee.konturtestapp.datasource.remote.PersonRemote
import com.krasjbee.konturtestapp.datasource.remote.mapToPerson
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val apiClient: PersonApiClient,
    private val pagingCache: PagingCache<Person>,
//    private val ioDispatcher : CoroutineDispatcher,
//    private val cpuDispatcher: CoroutineDispatcher
) : PersonRepository {
    private val files = listOf("generated-01.json", "generated-03.json", "generated-03.json")

    private var shouldFetch = true
    override suspend fun getPersonList(
        force: Boolean, pageSize: Int, page: Int
    ): Result<List<Person>> {
        return runCatching {
            if ((shouldFetch && pagingCache.isExpired) || force) {
                Log.i("CacheEvent", "getPersonList: data should be fetched")

                val files = files.map { processFile(it) }
                pagingCache.clear()
                files.forEach { pagingCache.addAll(it) }
                shouldFetch = false
            }
            pagingCache.getPage(pageSize, page)
        }
    }

    override suspend fun getPerson(personId: String): Result<Person> {
        return runCatching { pagingCache.getItem(personId) }
    }

    override suspend fun searchPersons(
        force: Boolean,
        searchQuery: String,
        pageSize: Int,
        page: Int
    ): Result<List<Person>> {
        return runCatching {
            if (force) {
                val files = files.map { processFile(it) }
                pagingCache.clear()
                files.forEach { pagingCache.addAll(it) }
                shouldFetch = false
            }
            pagingCache.searchItem(searchQuery, pageSize, page)
        }
    }

    private suspend fun processFile(filename: String): List<Person> { // TODO: change return type to result or either
        val response = apiClient.getPersonList(filename)
        val body = response.body()
        return coroutineScope {
            withContext(Dispatchers.Default) {
                if (response.isSuccessful && body != null) {
                    body.map(PersonRemote::mapToPerson)
                } else {
                    emptyList()
                }
            }
        }
    }
}