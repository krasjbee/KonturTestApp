package com.krasjbee.konturtestapp.data

import android.util.Log
import com.krasjbee.konturtestapp.data.cache.PagingCache
import com.krasjbee.konturtestapp.datasource.remote.PersonApiClient
import com.krasjbee.konturtestapp.datasource.remote.PersonRemote
import com.krasjbee.konturtestapp.datasource.remote.mapToPerson
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.util.suspendRunCatching
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val apiClient: PersonApiClient,
    private val pagingCache: PagingCache<Person>
) : PersonRepository {

    private var shouldFetch = true
    override suspend fun getPersonList(
        force: Boolean, pageSize: Int, page: Int
    ): Result<List<Person>> {
        return suspendRunCatching {
            if ((shouldFetch && pagingCache.isExpired) || force) {
                Log.i("CacheEvent", "getPersonList: data should be fetched")

                val response = apiClient.getPersonList("generated-01.json")
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.i("CacheEvent", "getPersonList: response is successful")
                    pagingCache.clear()
                    pagingCache.addAll(body.map(PersonRemote::mapToPerson))
                    shouldFetch = false
                }
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
        return runCatching { pagingCache.searchItem(searchQuery, pageSize, page) }
    }
}