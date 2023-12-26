package com.krasjbee.konturtestapp.ui.screens.persondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krasjbee.konturtestapp.domain.PersonRepository
import com.krasjbee.konturtestapp.ui.entities.PersonUI
import com.krasjbee.konturtestapp.ui.entities.mapToUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    private val repository: PersonRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: String = checkNotNull(savedStateHandle["personId"])

    init {
        getPersonById(id)
    }

    private val _screenState =
        MutableStateFlow<PersonDetailsState>(PersonDetailsState.PersonDetailsLoading)
    val screenState = _screenState.asStateFlow()

    private fun getPersonById(id: String) {
        viewModelScope.launch {
            val person = repository.getPerson(id)
            person
                .onSuccess {
                    _screenState.value = PersonDetailsState.PersonDetailsSuccess(it.mapToUi())
                }
                .onFailure {
                    _screenState.value = PersonDetailsState.PersonDetailsError()
                }
        }
    }
}

sealed interface PersonDetailsState {
    data object PersonDetailsLoading : PersonDetailsState
    class PersonDetailsSuccess(val person: PersonUI) : PersonDetailsState

    class PersonDetailsError() : PersonDetailsState
}