package com.krasjbee.konturtestapp.ui.screens.persondetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

private const val PERSON_DETAILS_ROUTE_PREFIX = "personDetails"
private const val PERSON_DETAILS_ID_ARG = "personId"

const val PERSON_DETAILS_ROUTE = "$PERSON_DETAILS_ROUTE_PREFIX/{$PERSON_DETAILS_ID_ARG}"
fun NavController.navigateToProductDetails(
    personId: String, navOptions: NavOptions? = null
) {
    val encodedId = URLEncoder.encode(personId, UTF_8.name())
    navigate(route = "$PERSON_DETAILS_ROUTE_PREFIX/$encodedId", navOptions = navOptions)
}

fun NavGraphBuilder.personDetailsScreen(
    navController: NavController
) {
    composable(
        route = PERSON_DETAILS_ROUTE,
        arguments = listOf(navArgument(PERSON_DETAILS_ID_ARG) {
            type = NavType.StringType
        })
    ) { navBackStackEntry ->
        val personId = navBackStackEntry.arguments?.getString(PERSON_DETAILS_ID_ARG)
        checkNotNull(personId) { "personId should not be null" }
        PersonDetailsScreen(onBackButtonClick = { navController.navigateUp() })
    }
}