package com.kevintresuelo.clinicus

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kevintresuelo.clinicus.ui.screens.catalog.CatalogScreen
import com.kevintresuelo.clinicus.ui.screens.catalog.accommodation.AccommodationScreen
import com.kevintresuelo.clinicus.ui.screens.catalog.contactlenspower.ContactLensPowerScreen
import com.kevintresuelo.lorem.ui.screens.splash.SplashScreen

const val SPLASH_SCREEN = "SplashScreen"
const val CATALOG_SCREEN = "CatalogScreen"
const val CONTACT_LENS_POWER_SCREEN = "ContactLensPowerScreen"
const val ACCOMMODATION_SCREEN = "AccommodationScreen"

@ExperimentalMaterialApi
fun NavGraphBuilder.clinicusGraph(appState: ClinicusAppState) {
    composable(SPLASH_SCREEN) {
        SplashScreen(appState = appState, openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(CATALOG_SCREEN) {
        CatalogScreen(appState = appState, navigate = { route -> appState.navigate(route) })
    }

    composable(CONTACT_LENS_POWER_SCREEN) {
        ContactLensPowerScreen(appState = appState, openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(ACCOMMODATION_SCREEN) {
        AccommodationScreen(appState = appState, openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}