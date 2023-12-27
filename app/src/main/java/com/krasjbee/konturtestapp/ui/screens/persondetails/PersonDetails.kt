package com.krasjbee.konturtestapp.ui.screens.persondetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krasjbee.konturtestapp.ui.entities.PersonUI
import com.krasjbee.konturtestapp.ui.theme.clickableText
import com.krasjbee.konturtestapp.ui.theme.grayText

private const val CLICKABLE_TEXT_TAG = "dial"

@Composable
fun PersonDetailsScreen(
    viewModel: PersonDetailsViewModel = hiltViewModel(),
    onBackButtonClick: () -> Unit
) {
    val screenState = viewModel.screenState.collectAsStateWithLifecycle()
    Column {
        DetailsTopBar(onBackButtonClick = onBackButtonClick)

        when (val detailsState = screenState.value) {
            is PersonDetailsState.PersonDetailsError -> {}
            PersonDetailsState.PersonDetailsLoading -> {}
            is PersonDetailsState.PersonDetailsSuccess ->
                DetailsSuccess(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    person = detailsState.person
                )
        }
    }
}

@Composable
private fun DetailsSuccess(
    modifier: Modifier = Modifier,
    person: PersonUI
) {
    Column(modifier = modifier) {
        val context = LocalContext.current
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = person.name, style = MaterialTheme.typography.titleLarge)
        val clickableText = buildAnnotatedString {
            val text = person.phone
            withStyle(style = SpanStyle(color = clickableText)) {
                pushStringAnnotation(tag = CLICKABLE_TEXT_TAG, annotation = text)
                append(text)
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        ClickableText(text = clickableText, onClick = { offset ->
            clickableText.getStringAnnotations(offset, offset).firstOrNull()?.let {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${person.phone}")
                }
                context.startActivity(intent)
            }
        }, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = person.temperament,
            style = MaterialTheme.typography.bodyMedium.copy(color = grayText)
        )
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = person.educationPeriodUi.formattedPeriod,
            style = MaterialTheme.typography.bodyMedium.copy(color = grayText)
        )
        Spacer(modifier = Modifier.size(8.dp))

        Text(text = person.biography, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DetailsTopBar(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        IconButton(onClick = onBackButtonClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

