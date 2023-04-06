package com.kevintresuelo.clinicus.ui.screens.catalog.contactlens

import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.judemanutd.katexview.KatexView
import com.kevintresuelo.clinicus.ClinicusAppState
import com.kevintresuelo.clinicus.R
import com.kevintresuelo.clinicus.utils.getValidatedAxis
import com.kevintresuelo.clinicus.utils.getValidatedPower
import java.text.DecimalFormat
import com.kevintresuelo.clinicus.R.string as AppStrings

@OptIn(ExperimentalMaterial3Api::class)
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

    val focusManager = LocalFocusManager.current
    val resources = LocalContext.current.resources

    var spectacleSphere by rememberSaveable { mutableStateOf("") }
    var spectacleCylinder by rememberSaveable { mutableStateOf("") }
    var spectacleAxis by rememberSaveable { mutableStateOf("") }

    var showCalculation by rememberSaveable { mutableStateOf(false) }

    val (errorMessage, setErrorMessage) = rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.77f)
                .clip(shape = CardDefaults.shape)
                .background(color = MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_spectacle_power),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        TextField(
            value = spectacleSphere,
            onValueChange = {
                spectacleSphere = getValidatedPower(it)
                showCalculation = false
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_sphere_title)) },
            suffix = { Text(text = stringResource(id = AppStrings.generic_sph_unit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = spectacleCylinder,
                onValueChange = {
                    spectacleCylinder = getValidatedPower(it)
                    showCalculation = false
                },
                modifier = Modifier
                    .weight(7f),
                label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_cylinder_title)) },
                suffix = { Text(text = stringResource(id = AppStrings.generic_cyl_unit)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            TextField(
                value = spectacleAxis,
                onValueChange = {
                    spectacleAxis = getValidatedAxis(it)
                    showCalculation = false
                },
                modifier = Modifier
                    .weight(3f),
                label = { Text(text = stringResource(id = AppStrings.tools_contact_lens_axis_title)) },
                prefix = { Text(text = stringResource(id = AppStrings.generic_axis_prefix)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Button(
            onClick = {
                val spectacleSphereFloat = spectacleSphere.toFloatOrNull()
                val spectacleCylinderFloat = spectacleCylinder.toFloatOrNull() ?: 0f
                val spectacleAxisInt = spectacleAxis.toIntOrNull() ?: 180

                if (spectacleSphereFloat == null) {
                    setErrorMessage(resources.getString(AppStrings.tools_contact_lens_error_msg_invalid_spec_sph))
                    return@Button
                }

                spectacleSphere = String.format("%.2f", spectacleSphereFloat)

                if (spectacleCylinderFloat == 0f) {
                    spectacleCylinder = ""
                    spectacleAxis = ""
                } else {
                    spectacleCylinder = String.format("%.2f", spectacleCylinderFloat)
                    spectacleAxis = spectacleAxisInt.toString()
                }

                showCalculation = !showCalculation

                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text(text = stringResource(id = AppStrings.tools_contact_lens_calculate))
        }

        if(showCalculation) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (spectacleCylinder.isNullOrEmpty()) {
                    SphereContactLensPower(
                        spectacleSphere = spectacleSphere
                    )
                } else {
                    SpherocylinderContactLensPower(
                        spectacleSphere = spectacleSphere,
                        spectacleCylinder = spectacleCylinder,
                        spectacleAxis = spectacleAxis,
                    )
                }
            }
        }

    }

    if (!errorMessage.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = {
                setErrorMessage(null)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        setErrorMessage(null)
                    }
                ) {
                    Text(text = stringResource(id = AppStrings.generic_error_action_ok))
                }
            },
            icon = { Icon(imageVector = Icons.Rounded.Error, contentDescription = null) },
            title = { Text(text = stringResource(id = AppStrings.generic_error_title)) },
            text = { Text(text = errorMessage) },
        )
    }

}

@Composable
fun SphereContactLensPower(
    spectacleSphere: String,
    modifier: Modifier = Modifier
) {
    val df = DecimalFormat("#.#####")

    val clSphere = df.format(spectacleSphere.toFloat()/(1-(df.format(0.012*spectacleSphere.toFloat()).toFloat())))

    val latex = "\$\$ \\begin{aligned}" +
            "F &= \\frac{SR}{1-(VD)(SR)} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{1-(0.012)(${spectacleSphere} \\space \\text{D} )} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{1-(${df.format(0.012*spectacleSphere.toFloat())} \\space \\text{D} )} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{${1-(df.format(0.012*spectacleSphere.toFloat()).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{F = ${String.format("%.2f", clSphere.toFloat())} \\space \\text{D} } \$\$"

    val latexColor = MaterialTheme.colorScheme.onBackground.toArgb()

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_effective_power),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(latex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(latex)
            }
        )
    }

    Text(
        text = "Spectacle Rx: $spectacleSphere D Sph",
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = "Contact lens Rx: ${String.format("%.2f", 0.25*(Math.round(clSphere.toFloat()/0.25)))} D Sph",
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun SpherocylinderContactLensPower(
    spectacleSphere: String,
    spectacleCylinder: String,
    spectacleAxis: String,
    modifier: Modifier = Modifier
) {
    val df = DecimalFormat("#.#####")

    val clSphere = df.format(spectacleSphere.toFloat()/(1-(df.format(0.012*spectacleSphere.toFloat()).toFloat())))

    val sphereMeridian = spectacleAxis.toFloat()
    val cylinderMeridian = if (sphereMeridian < 90f) sphereMeridian + 90f else sphereMeridian - 90f

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_optical_cross),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {

            Divider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .rotate(90f),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Divider(
                modifier = Modifier
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Divider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .rotate(cylinderMeridian),
                color = MaterialTheme.colorScheme.tertiary,
            )
            Divider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .rotate(sphereMeridian),
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }

    val sphereMeridianPowerLatex = "\$\$ \\begin{aligned}" +
            "M_${sphereMeridian} &= \\frac{SR}{1-(VD)(SR)} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{1-(0.012)(${spectacleSphere} \\space \\text{D} )} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{1-(${df.format(0.012*spectacleSphere.toFloat())} \\space \\text{D} )} \\\\" +
            "&= \\frac{${spectacleSphere} \\space \\text{D} }{${1-(df.format(0.012*spectacleSphere.toFloat()).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{F = ${String.format("%.2f", clSphere.toFloat())} \\space \\text{D} } \$\$"

    val sphereMeridianPowerLatexColor = MaterialTheme.colorScheme.secondary.toArgb()

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_effective_power),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(latex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(latex)
            }
        )
    }

    Text(
        text = "Spectacle Rx: $spectacleSphere D Sph = $spectacleCylinder D Cyl x $spectacleAxis",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = "Contact lens Rx: ${String.format("%.2f", 0.25*(Math.round(clSphere.toFloat()/0.25)))} D Sph",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium
    )
}