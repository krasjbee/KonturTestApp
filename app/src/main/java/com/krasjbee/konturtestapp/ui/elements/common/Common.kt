package com.krasjbee.konturtestapp.ui.elements.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        CircularProgressIndicator()
    }
}