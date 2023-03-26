package com.kevintresuelo.clinicus.ui.screens.catalog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevintresuelo.clinicus.ClinicusAppState
import com.kevintresuelo.clinicus.R.string as AppStrings

@Composable
fun CatalogScreen(
    appState: ClinicusAppState,
    navigate: (String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {

    appState.systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
    )

    appState.showTopAppBar(stringResource(id = AppStrings.app_name))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HelpCards(
            title = AppStrings.help_accommodation,
        ) {
            // navigate(EXERCISE_TRANAGLYPH_SCREEN)
        }
        HelpCards(
            title = AppStrings.help_accommodation,
        ) {
            // navigate(EXERCISE_PUSHUP_SCREEN)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpCards(@StringRes title: Int, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .widthIn(0.dp, 500.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = title),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}