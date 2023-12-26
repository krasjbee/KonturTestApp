package com.krasjbee.konturtestapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.krasjbee.konturtestapp.ui.screens.persondetails.personDetailsScreen
import com.krasjbee.konturtestapp.ui.screens.personlist.PERSON_LIST_ROUTE
import com.krasjbee.konturtestapp.ui.screens.personlist.personListScreen

@Composable
fun KonturTestAppNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = PERSON_LIST_ROUTE) {
        personListScreen(navController)
        personDetailsScreen(navController)
    }
}