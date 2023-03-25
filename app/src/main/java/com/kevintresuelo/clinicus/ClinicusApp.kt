package com.kevintresuelo.clinicus

import android.app.Activity
import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kevintresuelo.clinicus.components.snackbar.SnackbarManager
import com.kevintresuelo.clinicus.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import com.kevintresuelo.clinicus.R.string as AppStrings

@Composable
fun VisioApp(activity: Activity, widthSizeClass: WindowWidthSizeClass, heightSizeClass: WindowHeightSizeClass) {
    AppTheme {

    }
}

@Composable
fun rememberAppState(
    activity: Activity,
    widthSizeClass: WindowWidthSizeClass,
    heightSizeClass: WindowHeightSizeClass,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    systemUiController: SystemUiController = rememberSystemUiController(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    topAppBarTitle: MutableState<String> = remember { mutableStateOf(resources.getString(AppStrings.app_name)) },
    showTopAppBar: MutableState<Boolean> = remember { mutableStateOf(false) }
) =
    remember(activity, widthSizeClass, heightSizeClass, snackbarHostState, systemUiController, navController, snackbarManager, resources, coroutineScope, topAppBarTitle, showTopAppBar) {
        ClinicusAppState(activity, widthSizeClass, heightSizeClass, snackbarHostState, systemUiController, navController, snackbarManager, resources, coroutineScope, topAppBarTitle, showTopAppBar)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}