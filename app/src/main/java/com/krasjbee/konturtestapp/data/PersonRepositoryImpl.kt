package com.krasjbee.konturtestapp.data

import com.krasjbee.konturtestapp.data.cache.PagingCache
import com.krasjbee.konturtestapp.datasource.remote.PersonApiClient
import com.krasjbee.konturtestapp.datasource.remote.PersonRemote
import com.krasjbee.konturtestapp.datasource.remote.mapToPerson
import com.krasjbee.konturtestapp.domain.DataContainer
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.util.suspendRunCatching
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
    ): DataContainer<List<Person>> {
        if ((shouldFetch && pagingCache.isExpired) || force) {
            val fetchedDataResult = files.map { processFile(it) }
            val isSuccessFull = fetchedDataResult.all { it.isSuccess }
            return when {
                isSuccessFull -> {
                    refillCache(fetchedDataResult.map { it.getOrNull()!! })
                    return DataContainer.Data(pagingCache.getPage(pageSize, page))
                }

                !pagingCache.isEmpty -> {
                    DataContainer.DataWithError(
                        pagingCache.getPage(pageSize, page),
                        Exception("Stub")
                    )
                }

                else -> {
                    DataContainer.NoData(Exception("Stub"))
                }
            }
        }
        return DataContainer.Data(pagingCache.getPage(pageSize, page))
    }

    override suspend fun getPerson(personId: String): DataContainer<Person> {
        return DataContainer.Data(pagingCache.getItem(personId))
    }

    override suspend fun searchPersons(
        force: Boolean, searchQuery: String, pageSize: Int, page: Int
    ): DataContainer<List<Person>> {
        val fetchedDataResult = files.map { processFile(it) }
        val isSuccessful = fetchedDataResult.all { it.isSuccess }
        if (force) {
            return when {
                isSuccessful -> {
                    refillCache(fetchedDataResult.map { it.getOrNull()!! })
                    return DataContainer.Data(pagingCache.searchItem(searchQuery, pageSize, page))
                }

                !pagingCache.isEmpty -> DataContainer.DataWithError(
                    pagingCache.searchItem(
                        searchQuery,
                        pageSize,
                        page
                    ), Exception("Stub")
                )

                else -> DataContainer.NoData(Exception("Stub"))
            }
        }

        return DataContainer.Data(pagingCache.searchItem(searchQuery, pageSize, page))

    }

    private suspend fun processFile(filename: String): Result<List<Person>> {
        return suspendRunCatching {
            val response = apiClient.getPersonList(filename)
            val body = response.body()
            coroutineScope {
                withContext(Dispatchers.Default) {
                    if (response.isSuccessful && body != null) {
                        body.map(PersonRemote::mapToPerson)
                    } else {
                        emptyList() // TODO: check
                    }
                }
            }
        }
    }

    private suspend fun refillCache(data: List<List<Person>>) {
        pagingCache.clear()
        data.forEach {
            pagingCache.addAll(it)
        }
    }
}