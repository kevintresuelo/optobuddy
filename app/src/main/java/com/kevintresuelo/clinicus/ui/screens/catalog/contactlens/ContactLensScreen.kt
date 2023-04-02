package com.kevintresuelo.clinicus.ui.screens.catalog.contactlens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevintresuelo.clinicus.ClinicusAppState
import com.kevintresuelo.clinicus.resources
import com.kevintresuelo.clinicus.utils.getValidatedAxis
import com.kevintresuelo.clinicus.utils.getValidatedCylinder
import com.kevintresuelo.clinicus.utils.getValidatedSphere
import com.kevintresuelo.clinicus.R.string as AppStrings

@Composable
fun ContactLensScreen(
    appState: ClinicusAppState,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactLensViewModel = hiltViewModel()
) {

    appState.systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
    )

    appState.showTopAppBar(stringResource(id = AppStrings.tools_contact_lens_title))

    ContactLensPower()

}

@Composable
fun ContactLensPower() {

    val resources = resources()

    var spectacleSphere by remember { mutableStateOf("") }
    var spectacleCylinder by remember { mutableStateOf("") }
    var spectacleAxis by remember { mutableStateOf("") }

    var spectacleCylinderErrorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_spectacle_power),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        TextField(
            value = spectacleSphere,
            onValueChange = {
                spectacleSphere = getValidatedSphere(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_sphere_title)) },
            suffix = { Text(text = stringResource(id = AppStrings.generic_sph_unit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = spectacleCylinder,
                onValueChange = {
                    spectacleCylinder = getValidatedCylinder(
                        text = it,
                    )
                },
                modifier = Modifier
                    .weight(5f),
                label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_cylinder_title)) },
                suffix = { Text(text = stringResource(id = AppStrings.generic_cyl_unit)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
            TextField(
                value = spectacleAxis,
                onValueChange = {
                    spectacleAxis = getValidatedAxis(it)
                },
                modifier = Modifier
                    .weight(2f),
                label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_axis_title)) },
                prefix = { Text(text = stringResource(id = AppStrings.generic_axis_prefix)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
        }

    }

}