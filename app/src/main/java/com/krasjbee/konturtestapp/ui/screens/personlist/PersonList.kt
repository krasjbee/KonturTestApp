package com.krasjbee.konturtestapp.ui.screens.personlist

import android.util.Log
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.krasjbee.konturtestapp.ui.theme.KonturTestAppTheme


@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel
) {

    val personList = viewModel.items.collectAsLazyPagingItems()
    Log.d("no data", "PersonListScreen: ${personList.loadState} ")
    if (personList.loadState.refresh == LoadState.Loading) {
        CircularProgressIndicator()
    }
    if (personList.itemCount > 0) {
        LazyColumn {
            items(personList.itemCount, key = {
                checkNotNull(personList[it]).id
            }) { index ->
                val person = personList[index]
                if (person != null) {
                    PersonItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = person.name,
                        phone = person.phone,
                        height = person.height
                    )
                }
                if (index != personList.itemCount - 1) {
                    Divider()
                }

            }
            if (personList.loadState.prepend == LoadState.Loading){
                item {
                    CircularProgressIndicator()
                }
            }
            if (personList.loadState.append == LoadState.Loading){
                item {
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@Composable
fun PersonItem(
    modifier: Modifier = Modifier, name: String, phone: String, height: String
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = name)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = phone)
        }
        Text(text = height)
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