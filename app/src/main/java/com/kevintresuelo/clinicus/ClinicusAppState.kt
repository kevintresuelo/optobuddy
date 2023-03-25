package com.kevintresuelo.clinicus

import android.app.Activity
import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.kevintresuelo.clinicus.components.snackbar.SnackbarManager
import com.kevintresuelo.clinicus.components.snackbar.SnackbarMessage.Companion.toAction
import com.kevintresuelo.clinicus.components.snackbar.SnackbarMessage.Companion.toActionText
import com.kevintresuelo.clinicus.components.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class ClinicusAppState(
    val activity: Activity,
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass,
    val snackbarHostState: SnackbarHostState,
    val systemUiController: SystemUiController,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope,
    val topAppBarTitle: MutableState<String>,
    val showTopAppBar: MutableState<Boolean>
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                val actionText = snackbarMessage.toActionText(resources)

                val snackbarResult = snackbarHostState.showSnackbar(
                    message = text,
                    actionLabel = actionText
                )
                when (snackbarResult) {
                    SnackbarResult.ActionPerformed -> snackbarMessage.toAction()
                    SnackbarResult.Dismissed -> Unit
                }
            }
        }
    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }

    fun showTopAppBar(title: String) {
        showTopAppBar.value = true
        topAppBarTitle.value = title
    }

    fun hideTopAppBar() {
        showTopAppBar.value = false
    }
}