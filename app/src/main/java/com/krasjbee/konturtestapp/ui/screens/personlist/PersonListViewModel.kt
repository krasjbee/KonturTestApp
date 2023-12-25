package com.krasjbee.konturtestapp.ui.screens.personlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
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
    private val pagingConfig = PagingConfig(pageSize = 20)
    val items = _searchQuery.debounce(100).flatMapLatest { query ->
        if (query.isBlank()) {
            Pager(config = pagingConfig, pagingSourceFactory = {
                PersonPagingSource { pageSize, page ->
                    repository.getPersonList(pageSize = pageSize, page = page)
                }
            })
        } else {
            Pager(config = pagingConfig, pagingSourceFactory = {
                PersonPagingSource { pageSize, page ->
                    repository.searchPersons(query, pageSize, page)
                }
            })
        }.flow.map { value: PagingData<Person> ->
            value.map { it.mapToUi() }
        }
    }.cachedIn(viewModelScope)


}