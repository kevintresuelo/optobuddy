package com.kevintresuelo.clinicus

import android.app.Activity
import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kevintresuelo.clinicus.components.snackbar.SnackbarManager
import com.kevintresuelo.clinicus.ui.theme.AppTheme
import com.kevintresuelo.clinicus.utils.billing.PurchaseHelper
import kotlinx.coroutines.CoroutineScope
import com.kevintresuelo.clinicus.R.string as AppStrings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClinicusApp(activity: Activity, widthSizeClass: WindowWidthSizeClass, heightSizeClass: WindowHeightSizeClass, purchaseHelper: PurchaseHelper) {
    AppTheme {
        val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState(activity, widthSizeClass, heightSizeClass, purchaseHelper)

            var canPop by remember { mutableStateOf(false) }

            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            DisposableEffect(appState.navController) {
                val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
                    canPop = controller.previousBackStackEntry != null
                }
                appState.navController.addOnDestinationChangedListener(listener)
                onDispose {
                    appState.navController.removeOnDestinationChangedListener(listener)
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = appState.snackbarHostState,
                        modifier = Modifier
                            .padding(8.dp)
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                        }
                    )
                },
                topBar = {
                    if (appState.showTopAppBar.value) {
                        var topAppBarDropDownMenuExpanded by remember {
                            mutableStateOf(false)
                        }
                        TopAppBar(
                            title = {
                                Text(text = appState.topAppBarTitle.value)
                            },
                            navigationIcon = {
                                if (canPop) {
                                    IconButton(onClick = { appState.navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.Rounded.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            actions = {
                                when (currentDestination?.route) {
                                    CATALOG_SCREEN -> {
                                        IconButton(onClick = {
                                            topAppBarDropDownMenuExpanded = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "More"
                                            )
                                        }
                                        DropdownMenu(expanded =
                                        topAppBarDropDownMenuExpanded, onDismissRequest =
                                        { topAppBarDropDownMenuExpanded = false }) {
                                            DropdownMenuItem(
                                                text = { Text(text = "Buy Pro") },
                                                onClick = {
                                                    appState.purchaseHelper.makePurchase()
                                                })
                                        }
                                    }
                                    else -> {

                                    }
                                }
                            },
                            scrollBehavior = topAppBarScrollBehavior
                        )
                    }
                }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    clinicusGraph(appState)
                }
            }
        }

    }
}

@Composable
fun rememberAppState(
    activity: Activity,
    widthSizeClass: WindowWidthSizeClass,
    heightSizeClass: WindowHeightSizeClass,
    purchaseHelper: PurchaseHelper,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    systemUiController: SystemUiController = rememberSystemUiController(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    topAppBarTitle: MutableState<String> = remember { mutableStateOf(resources.getString(AppStrings.app_name)) },
    showTopAppBar: MutableState<Boolean> = remember { mutableStateOf(false) },
) =
    remember(activity, widthSizeClass, heightSizeClass, snackbarHostState, systemUiController, navController, snackbarManager, resources, coroutineScope, topAppBarTitle, showTopAppBar, purchaseHelper) {
        ClinicusAppState(activity, widthSizeClass, heightSizeClass, snackbarHostState, systemUiController, navController, snackbarManager, resources, coroutineScope, topAppBarTitle, showTopAppBar, purchaseHelper)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}