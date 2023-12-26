package com.krasjbee.konturtestapp.ui.screens.personlist

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
//import androidx.compose.material.TextField
//import androidx.compose.material.TextFieldColors
//import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
//import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krasjbee.konturtestapp.R
import com.krasjbee.konturtestapp.ui.entities.PersonUI
import com.krasjbee.konturtestapp.ui.screens.persondetails.PersonDetailsScreen
import com.krasjbee.konturtestapp.ui.screens.persondetails.PersonDetailsViewModel
import com.krasjbee.konturtestapp.ui.theme.KonturTestAppTheme
import com.krasjbee.konturtestapp.ui.theme.grayText
import com.krasjbee.konturtestapp.ui.theme.primary


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel
) {
    Column {
        val query = viewModel.searchQuery.collectAsStateWithLifecycle()

        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .background(primary)
                .padding(horizontal = 16.dp),
            query = query.value,
            onQueryChange = { viewModel.setQuery(it) },
            placeholderText = stringResource(R.string.search_placeholder),
            onQueryClear = { viewModel.clearQuery() }
        )

        val personList = viewModel.items.collectAsLazyPagingItems()

        val loadingState =
            remember { derivedStateOf { personList.loadState.refresh == LoadState.Loading } }
        val pullRefreshState = rememberPullRefreshState(refreshing = loadingState.value,
            onRefresh = { personList.refresh() })

        val show = rememberSaveable {
            mutableStateOf(false)
        }

        if (!show.value) {
            RefreshableList(
                modifier = Modifier.fillMaxSize(),
                personList = personList,
                pullRefreshState = pullRefreshState,
                loadingState = loadingState,
                onItemClick = {
                    show.value = true

                }
            )
        } else {
            PersonDetailsScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel<PersonDetailsViewModel>(
                ) // TODO: remove
            )
        }

    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun RefreshableList(
    modifier: Modifier = Modifier,
    personList: LazyPagingItems<PersonUI>,
    pullRefreshState: PullRefreshState,
    loadingState: State<Boolean>,
    onItemClick: (id: String) -> Unit
) {
    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        Log.d("loadingState", "RefreshableList: ${personList.loadState} ")

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = loadingState.value,
            state = pullRefreshState,
            contentColor = MaterialTheme.colorScheme.primary
        )

        val isLoading =
            personList.loadState.refresh == LoadState.Loading || personList.loadState.source.refresh == LoadState.Loading
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }


        if (personList.itemCount > 0) {
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(personList.itemCount,
                    key = personList.itemKey { it.id }
                ) { index ->
                    val person = personList[index]
                    if (person != null) {
                        PersonItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(person.id) }
                                .padding(16.dp),
                            name = person.name,
                            phone = person.phone,
                            height = person.height
                        )
                    }
                    if (index != personList.itemCount - 1) {
                        Divider()
                    }

                }
                if (personList.loadState.source.prepend == LoadState.Loading) {
                    item {
                        CircularProgressIndicator()
                    }
                }
                if (personList.loadState.source.append == LoadState.Loading) {
                    item {
                        CircularProgressIndicator()
                    }
                }
            }

        }
    }
}


@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onQueryClear: () -> Unit,
    placeholderText: String
) {
    Box(modifier = modifier) {
        TextField(modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            shape = RectangleShape,
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = onQueryClear) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
            ),
            placeholder = { Text(text = placeholderText) },
            // TODO: placeholder colors?
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedPlaceholderColor = grayText,
                unfocusedPlaceholderColor = grayText,
                unfocusedIndicatorColor = grayText,
                focusedLeadingIconColor = grayText,
                unfocusedLeadingIconColor = grayText,
                disabledLeadingIconColor = grayText,
                errorLeadingIconColor = grayText

            )
        )
    }
}

@Composable
fun PersonItem(
    modifier: Modifier = Modifier, name: String, phone: String, height: String
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = phone, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = height, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showSystemUi = true)
@Composable
fun PersonItemPreview() {
    KonturTestAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PersonItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                name = "Name",
                phone = "8 - 800 -555 -35 -35",
                height = 123.4.toString()
            )
        }
    }
}