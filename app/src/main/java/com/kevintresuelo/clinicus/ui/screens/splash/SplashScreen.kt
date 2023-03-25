package com.kevintresuelo.lorem.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevintresuelo.clinicus.ClinicusAppState
import com.kevintresuelo.clinicus.ui.screens.splash.SplashViewModel
import com.kevintresuelo.clinicus.utils.annotations.DevicesPreview
import kotlinx.coroutines.delay
import com.kevintresuelo.clinicus.R.string as AppStrings

private const val SPLASH_TIMEOUT = 1000L

@Composable
fun SplashScreen(
    appState: ClinicusAppState,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {

    appState.systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
    )

    appState.hideTopAppBar()

    SplashScreenUI(modifier)

    if (viewModel.doneLoadingPatient && viewModel.doneLoadingOnboarding) {
        LaunchedEffect(true) {
            delay(SPLASH_TIMEOUT)
            viewModel.onAppStart(openAndPopUp)
        }
    }
}

@Composable
private fun SplashScreenUI(modifier: Modifier) {
    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.HealthAndSafety,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        Text(
            text = stringResource(AppStrings.app_name),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.size(36.dp))
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary,
            strokeCap = StrokeCap.Round
        )
    }
}

@DevicesPreview
@Composable
fun SplashScreenPreview() {
    SplashScreenUI(modifier = Modifier)
}