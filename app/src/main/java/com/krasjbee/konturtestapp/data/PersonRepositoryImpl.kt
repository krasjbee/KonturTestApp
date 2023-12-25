package com.krasjbee.konturtestapp.data

import com.krasjbee.konturtestapp.data.cache.PagingCache
import com.krasjbee.konturtestapp.datasource.remote.PersonApiClient
import com.krasjbee.konturtestapp.datasource.remote.PersonRemote
import com.krasjbee.konturtestapp.datasource.remote.mapToPerson
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val apiClient: PersonApiClient,
    private val pagingCache: PagingCache<Person>
) : PersonRepository {

    private var shouldFetch = true
    override fun getPersonList(
        force: Boolean, pageSize: Int, page: Int
    ): Flow<Result<List<Person>>> {
        return flow {
            if ((shouldFetch && pagingCache.isExpired) || force) {
                val data = runCatching {
                    val response = apiClient.getPersonList("")
                    val body = response.body()
                    shouldFetch = false
                    checkNotNull(body)
                }
                data.onSuccess {
                    pagingCache.clear()
                    pagingCache.addAll(it.map(PersonRemote::mapToPerson) )
                }.onFailure {
//                    emit(data)
                }
            }
            if (!pagingCache.isEmpty) { emit(Result.success(pagingCache.getPage(pageSize, page))) }
        }
    }

    override fun getPerson(personId: String): Flow<Result<Person>> {
        TODO("Not yet implemented")
    }

    override fun searchPersons(searchQuery: String): Flow<Result<List<Person>>> {
        TODO("Not yet implemented")
    }
}