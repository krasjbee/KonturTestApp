package com.krasjbee.konturtestapp.ui.screens.personlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.krasjbee.konturtestapp.R
import com.krasjbee.konturtestapp.ui.entities.PersonUI
import com.krasjbee.konturtestapp.ui.theme.KonturTestAppTheme
import com.krasjbee.konturtestapp.ui.theme.grayText
import com.krasjbee.konturtestapp.ui.theme.primary


@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel = hiltViewModel(), onItemClick: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val queryState = viewModel.searchQuery.collectAsStateWithLifecycle()
    val personList = viewModel.items.collectAsLazyPagingItems()
    val fetchedState = viewModel.fetchState.collectAsStateWithLifecycle()

    if (fetchedState.value is PersonListScreenUiState.NoData || fetchedState.value is PersonListScreenUiState.CachedDataWithError) {
        val errorMessage = stringResource(R.string.no_connection)
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                errorMessage, duration = SnackbarDuration.Indefinite
            )
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState) {
            Snackbar(snackbarData = it, contentColor = Color.White)
        }
    }) { paddingValues ->
        PersonListScreenContent(
            modifier = Modifier.padding(paddingValues),
            queryState = queryState,
            onQueryChange = { query -> viewModel.setQuery(query) },
            onQueryClear = { viewModel.clearQuery() },
            personList = personList,
            onItemClick = onItemClick
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PersonListScreenContent(
    modifier: Modifier = Modifier,
    queryState: State<String>,
    onQueryChange: (String) -> Unit,
    onQueryClear: () -> Unit,
    personList: LazyPagingItems<PersonUI>,
    onItemClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .background(primary)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            query = queryState.value,
            onQueryChange = onQueryChange,
            placeholderText = stringResource(R.string.search_placeholder),
            onQueryClear = onQueryClear
        )


        val loadingState =
            remember { derivedStateOf { personList.loadState.refresh == LoadState.Loading } }
        val pullRefreshState = rememberPullRefreshState(
            refreshing = loadingState.value,
            onRefresh = { personList.refresh() })

        RefreshableList(
            modifier = Modifier.fillMaxSize(),
            personList = personList,
            pullRefreshState = pullRefreshState,
            loadingState = loadingState,
            queryState = queryState,
            onItemClick = onItemClick
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun RefreshableList(
    modifier: Modifier = Modifier,
    personList: LazyPagingItems<PersonUI>,
    pullRefreshState: PullRefreshState,
    loadingState: State<Boolean>,
    queryState: State<String>,
    onItemClick: (id: String) -> Unit
) {
    BoxWithConstraints(modifier = modifier.pullRefresh(pullRefreshState)) {
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = loadingState.value,
            state = pullRefreshState,
            contentColor = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.height(this.maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(personList.itemCount, key = personList.itemKey { it.id }) { index ->
                val person = personList[index]
                if (person != null) {
                    PersonItem(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(person.id) }
                        .padding(16.dp),
                        name = person.name,
                        phone = person.phone,
                        height = person.height)
                }
                if (index != personList.itemCount - 1) {
                    Divider()
                }
            }
            if (personList.loadState.source.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator()
                }
            }
        }
        if (queryState.value.isNotBlank() && personList.itemCount == 0) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.nothig_was_found),
                style = MaterialTheme.typography.bodyLarge
            )
        }

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
    }
}


@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onQueryClear: () -> Unit,
    placeholderText: String
) {
    Box(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
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
private fun PersonItem(
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