package com.krasjbee.konturtestapp.ui.screens.personlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.krasjbee.konturtestapp.domain.DataHolder
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.ui.entities.mapToUi
import com.krasjbee.konturtestapp.ui.paging.ForceRefreshMediator
import com.krasjbee.konturtestapp.ui.paging.GenericPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

typealias SearchCall = suspend (isForce: Boolean, query: String, pageSize: Int, page: Int) -> DataHolder<List<Person>>
typealias FetchCall = suspend (isForce: Boolean, pageSize: Int, page: Int) -> DataHolder<List<Person>>

@HiltViewModel
class PersonListViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = false)

    private val _fetchState =
        MutableStateFlow<PersonListScreenUiState>(PersonListScreenUiState.Initial)
    val fetchState = _fetchState.distinctUntilChangedBy { it::class }.stateIn(
        viewModelScope,
        SharingStarted.Lazily, PersonListScreenUiState.Initial
    )


    fun setQuery(newQuery: String) {
        _searchQuery.update {
            newQuery
        }
    }

    fun clearQuery() {
        setQuery("")
    }


    val items = _searchQuery.debounce(500).flatMapLatest { query ->
        createPager(
            query,
            pagingConfig,
            searchCall = repository::searchPersons,
            pageFetch = repository::getPersonList,
            onErrorOccurred = { error ->
                _fetchState.update {
                    PersonListScreenUiState.NoData(error)
                }
            },
            onDataFetched = {
                _fetchState.update {
                    PersonListScreenUiState.FetchedData
                }
            },
            onCachedData = { _, error ->
                _fetchState.update {
                    PersonListScreenUiState.CachedDataWithError(error)
                }
            }
        ).flow.map { pagingData: PagingData<Person> ->
            pagingData.map(Person::mapToUi)
        }
    }.cachedIn(viewModelScope)


    //this is such a mess, mistakes were made ¯\_(ツ)_/¯
    /**
     * creates pager according to query, and signals about loading states via on**SomethigHappend**
     *
     */
    @OptIn(ExperimentalPagingApi::class)
    private fun createPager(
        query: String,
        pagingConfig: PagingConfig,
        searchCall: SearchCall,
        pageFetch: FetchCall,
        onErrorOccurred: (error: Throwable?) -> Unit = {},
        onDataFetched: (data: List<Person>) -> Unit = {},
        onCachedData: (data: List<Person>, error: Throwable?) -> Unit = { _, _ -> }
    ): Pager<Int, Person> {
        return if (query.isBlank()) {
            Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    GenericPagingSource { pageSize, page ->
                        pageFetch(false, pageSize, page)
                            .onDataWithError(onCachedData)
                            .onData(onDataFetched)
                            .onNoData(onErrorOccurred)
                    }
                },
                remoteMediator = ForceRefreshMediator { isForce, pageSize, page ->
                    pageFetch(isForce, pageSize, page)
                        .onDataWithError(onCachedData)
                        .onData(onDataFetched)
                        .onNoData(onErrorOccurred)
                }
            )
        } else {
            Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    GenericPagingSource { pageSize, page ->
                        searchCall(false, query, pageSize, page)
                            .onDataWithError(onCachedData)
                            .onData(onDataFetched)
                            .onNoData(onErrorOccurred)
                    }
                },
                remoteMediator = ForceRefreshMediator { isForce, pageSize, page ->
                    searchCall(isForce, query, pageSize, page)
                        .onDataWithError(onCachedData)
                        .onData(onDataFetched)
                        .onNoData(onErrorOccurred)
                }
            )
        }
    }
}

//no need to encapsulate paging data because it will ruin paging ¯\_(ツ)_/¯
sealed interface PersonListScreenUiState {
    class CachedDataWithError(val error: Throwable?) : PersonListScreenUiState

    data object FetchedData : PersonListScreenUiState

    class NoData(val error: Throwable?) : PersonListScreenUiState

    data object Initial : PersonListScreenUiState
}