package com.krasjbee.konturtestapp.ui.screens.persondetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krasjbee.konturtestapp.ui.entities.PersonUI
import com.krasjbee.konturtestapp.ui.theme.grayText

@Composable
fun PersonDetailsScreen(
    viewModel: PersonDetailsViewModel
) {
    val screenState = viewModel.screenState.collectAsStateWithLifecycle()
    when (screenState.value) {
        is PersonDetailsState.PersonDetailsError -> {}
        PersonDetailsState.PersonDetailsLoading -> {}
        is PersonDetailsState.PersonDetailsSuccess ->
            DetailsSuccess(
                modifier = Modifier.padding(horizontal = 16.dp),
                person = (screenState.value as PersonDetailsState.PersonDetailsSuccess).person
            )
    }
}

@Composable
private fun DetailsSuccess(
    modifier: Modifier = Modifier,
    person: PersonUI
) {
    Column(modifier = modifier) {
        val context = LocalContext.current
        Text(text = person.name, style = MaterialTheme.typography.titleLarge)
        val clickableText = buildAnnotatedString {
            val text = person.phone
            withStyle(style = SpanStyle(color = Color(0xff269df7))) {
                pushStringAnnotation(tag = "dial", annotation = text)
                append(text)
            }
        }
        ClickableText(text = clickableText, onClick = { offset ->
            clickableText.getStringAnnotations(offset, offset).firstOrNull()?.let {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${person.phone}")
                }
                context.startActivity(intent)
            }
        }, style = MaterialTheme.typography.bodyMedium)
//        Text(text = person.phone)
        Text(
            text = person.temperament,
            style = MaterialTheme.typography.bodyMedium.copy(color = grayText)
        )
        Text(
            text = person.educationPeriodUi.formattedPeriod,
            style = MaterialTheme.typography.bodyMedium.copy(color = grayText)
        )
        Text(text = person.biography, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DetailsTopBar(
    modifier: Modifier = Modifier
) {
    Row {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
    }
}

