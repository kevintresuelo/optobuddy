package com.kevintresuelo.clinicus

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kevintresuelo.lorem.ui.screens.splash.SplashScreen

const val SPLASH_SCREEN = "SplashScreen"

@ExperimentalMaterialApi
fun NavGraphBuilder.clinicusGraph(appState: ClinicusAppState) {
    composable(SPLASH_SCREEN) {
        SplashScreen(appState = appState, openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}