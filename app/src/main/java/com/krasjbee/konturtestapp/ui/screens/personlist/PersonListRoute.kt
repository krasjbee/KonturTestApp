package com.krasjbee.konturtestapp.ui.screens.personlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.krasjbee.konturtestapp.ui.screens.persondetails.navigateToProductDetails

const val PERSON_LIST_ROUTE = "personList"

fun NavController.navigateToPersonList(
    navOptions: NavOptions? = null
) {
    navigate(route = PERSON_LIST_ROUTE, navOptions = navOptions)
}

fun NavGraphBuilder.personListScreen(
    navController: NavHostController
) {
    composable(
        route = PERSON_LIST_ROUTE,
    ) { backStackEntry ->
        PersonListScreen(onItemClick = { id ->
            navController.navigateToProductDetails(personId = id)
        })
    }
}