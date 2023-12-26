package com.krasjbee.konturtestapp.ui.screens.personlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.krasjbee.konturtestapp.data.ForceRefreshMediator
import com.krasjbee.konturtestapp.data.PersonPagingSource
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.ui.entities.mapToUi
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

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun setSearching(newIsSearching: Boolean) {
        _isSearching.value = newIsSearching
    }

    @OptIn(ExperimentalPagingApi::class)
    val items = _searchQuery.debounce(500).flatMapLatest { query ->
//        val call: suspend (pageSize: Int, page: Int) -> Result<List<Person>> =
//            if (query.isBlank()) {
//                { pageSize: Int, page: Int ->
//                    repository.getPersonList(pageSize = pageSize, page = page)
//                }
//            } else {
//                { pageSize: Int, page: Int ->
//                    repository.searchPersons(searchQuery = query, pageSize = pageSize, page = page)
//                }
//            }
//
//        Pager(
//            config = pagingConfig,
//            pagingSourceFactory = { PersonPagingSource(call) },
//            remoteMediator = Mediator(onRefreshCallback = { page, pageSize ->
//                repository.getPersonList(force = true, pageSize, page)
//            }, pageFetchCallback = { pageSize, page ->
//                repository.getPersonList(
//                    pageSize = pageSize, page = page
//                )
//            })
//        ).flow.map { pagingData -> pagingData.map(Person::mapToUi) }

        if (query.isBlank()) {
            Pager(config = pagingConfig, pagingSourceFactory = {
                PersonPagingSource { pageSize, page ->
                    repository.getPersonList(pageSize = pageSize, page = page)
                }
            }, remoteMediator = ForceRefreshMediator(onRefreshCallback = { page, pageSize ->
                repository.getPersonList(force = true, pageSize, page)
            }, pageFetchCallback = { pageSize, page ->
                repository.getPersonList(
                    pageSize = pageSize, page = page
                )
            }))
        } else {
            Pager(config = pagingConfig, pagingSourceFactory = {
                PersonPagingSource { pageSize, page ->
                    repository.searchPersons(
                        searchQuery = query,
                        pageSize = pageSize,
                        page = page
                    )
                }
            }, remoteMediator = ForceRefreshMediator(onRefreshCallback = { page, pageSize ->
                repository.searchPersons(
                    force = true,
                    searchQuery = query,
                    pageSize = pageSize,
                    page = page
                ) // TODO: fix refresh
            }, pageFetchCallback = { pageSize, page ->
                repository.searchPersons(
                    searchQuery = query, pageSize = pageSize, page = page
                )
            })
            )
        }.flow.map { value: PagingData<Person> ->
            value.map { it.mapToUi() }
        }
    }.cachedIn(viewModelScope)

}