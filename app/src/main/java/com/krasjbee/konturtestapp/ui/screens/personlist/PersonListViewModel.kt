package com.krasjbee.konturtestapp.ui.screens.personlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.krasjbee.konturtestapp.domain.DataContainer
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.ui.entities.mapToUi
import com.krasjbee.konturtestapp.ui.paging.ForceRefreshMediator
import com.krasjbee.konturtestapp.ui.paging.GenericPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PersonListViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = false)

    fun setQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearQuery() {
        setQuery("")
    }

    private val _error = MutableStateFlow<Exception?>(null)
    val error = _error.asStateFlow()

    val items = _searchQuery.debounce(500).flatMapLatest { query ->
        createPager(
            query,
            pagingConfig,
            searchCall = { isForce, searchQuery, pageSize, page ->
                repository.searchPersons(isForce, searchQuery, pageSize, page)
            },
            pageFetch = { force, pageSize, page ->
                repository.getPersonList(force, pageSize, page)
            },
            onErrorOccurred = {
                _error.value = it
                Log.d("errorhandling", " ${it.message ?: "No message"} ")
            }
        ).flow.map { value: PagingData<Person> ->
            value.map { it.mapToUi() }
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    private fun createPager(
        query: String,
        pagingConfig: PagingConfig,
        searchCall: suspend (isForce: Boolean, query: String, pageSize: Int, page: Int) -> DataContainer<List<Person>>,
        pageFetch: suspend (isForce: Boolean, pageSize: Int, page: Int) -> DataContainer<List<Person>>,
        onErrorOccurred: (Exception) -> Unit = {}
    ): Pager<Int, Person> {
        return if (query.isBlank()) {
            Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    GenericPagingSource { pageSize, page ->
                        pageFetch(
                            false, pageSize, page
                        ).onHasError(onErrorOccurred)
                    }
                },
                remoteMediator = ForceRefreshMediator { isForce, pageSize, page ->
                    pageFetch(isForce, pageSize, page).onHasError(
                        onErrorOccurred
                    )
                }
            )
        } else {
            Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    GenericPagingSource { pageSize, page ->
                        searchCall(
                            false, query, pageSize, page
                        ).onHasError(onErrorOccurred)
                    }
                },
                remoteMediator = ForceRefreshMediator { isForce, pageSize, page ->
                    searchCall(
                        isForce, query, pageSize, page
                    ).onHasError(onErrorOccurred)
                }
            )
        }
    }

}