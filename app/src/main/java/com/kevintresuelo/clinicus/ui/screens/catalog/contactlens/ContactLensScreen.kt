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
import com.kevintresuelo.clinicus.utils.*
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
                var spectacleSphereFloat = spectacleSphere.toFloatOrNull()
                var spectacleCylinderFloat = spectacleCylinder.toFloatOrNull() ?: 0f
                var spectacleAxisInt = spectacleAxis.toIntOrNull() ?: 180

                /**
                 * If the user has only inputted a cylinder power, leaving the sphere
                 * blank, we presume that the sphere is zero/plano.
                 */
                if (spectacleCylinderFloat != 0f && spectacleSphereFloat == null) {
                    spectacleSphereFloat = 0f
                }

                /**
                 * If, at this point, the sphere is still null, we show an error to the
                 * user indicating that the user must provide at least the sphere power
                 * to continue.
                 */
                if (spectacleSphereFloat == null) {
                    setErrorMessage(resources.getString(AppStrings.tools_contact_lens_error_msg_invalid_spec_sph))
                    return@Button
                }

                spectacleSphere = formatDiopter(spectacleSphereFloat)

                /**
                 * If the cylinder power is zero, we erase the cylinder power and
                 * the axis.
                 *
                 * If not, we format it according to how we format powers.
                 */
                if (spectacleCylinderFloat == 0f) {
                    spectacleCylinder = ""
                    spectacleAxis = ""
                } else {
                    spectacleCylinder = formatDiopter(spectacleCylinderFloat)
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

    val clPowerLatex = "\$\$ \\begin{aligned}" +
            "F &= \\frac{SR}{1-(VD)(SR)} \\\\" +
            "&= \\frac{ $spectacleSphere \\space \\text{D} }{1-(0.012)( $spectacleSphere \\space \\text{D} )} \\\\" +
            "&= \\frac{ $spectacleSphere \\space \\text{D} }{1-(${df.format(0.012*spectacleSphere.toFloat())} \\space \\text{D} )} \\\\" +
            "&= \\frac{ $spectacleSphere \\space \\text{D} }{${1-(df.format(0.012*spectacleSphere.toFloat()).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{F = ${formatDiopter(clSphere.toFloat())} \\space \\text{D} } \$\$"

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

                view.setText(clPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(clPowerLatex)
            }
        )
    }

    Text(
        text = "Spectacle Rx: $spectacleSphere D Sph",
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = "Contact lens Rx: ${formatDiopter(0.25*(Math.round(clSphere.toFloat()/0.25)))} D Sph",
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
    val clCylinder = df.format((spectacleSphere.toFloat()+spectacleCylinder.toFloat())/(1-(df.format(0.012*(spectacleSphere.toFloat()+spectacleCylinder.toFloat())).toFloat())))

    val sphereMeridianPower = spectacleSphere.toFloat()
    val cylinderMeridianPower = spectacleSphere.toFloat() + spectacleCylinder.toFloat()

    val sphereMeridian = spectacleAxis.toInt()
    val cylinderMeridian = if (sphereMeridian < 90) sphereMeridian + 90 else sphereMeridian - 90

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_spectacle_optical_cross),
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
                    .fillMaxWidth(0.75f)
                    .rotate(-cylinderMeridian.toFloat()),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Divider(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.75f)
                    .rotate(-sphereMeridian.toFloat()),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }

    val latexColor = MaterialTheme.colorScheme.onBackground.toArgb()

    val sphereMeridianPowerLatexColorHex = argbToHex(MaterialTheme.colorScheme.secondary.toArgb()).drop(2)
    val sphereMeridianPowerLatex = "\$\$ \\begin{aligned} " +
            "\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}} &= Sph \\\\" +
            "&= $spectacleSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}} = ${formatDiopter(spectacleSphere.toFloat(), withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(sphereMeridianPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(sphereMeridianPowerLatex)
            }
        )
    }

    val cylinderMeridianPowerLatexColorHex = argbToHex(MaterialTheme.colorScheme.tertiary.toArgb()).drop(2)
    val cylinderMeridianPowerLatex = "\$\$ \\begin{aligned} " +
            "\\textcolor{#$cylinderMeridianPowerLatexColorHex}{M_{$cylinderMeridian}} &= Sph + Cyl \\\\" +
            "&= $spectacleSphere \\space \\text{D} + $spectacleCylinder \\space \\text{D} \\\\" +
            "&= ${formatDiopter(spectacleSphere.toFloat() + spectacleCylinder.toFloat())} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$cylinderMeridianPowerLatexColorHex}{M_{$cylinderMeridian}} = ${formatDiopter(power = spectacleSphere.toFloat() + spectacleCylinder.toFloat(), withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(cylinderMeridianPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(cylinderMeridianPowerLatex)
            }
        )
    }

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_effective_power),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )

    val clSphereMeridianPowerLatex = "\$\$ \\begin{aligned}" +
            "\\textcolor{#$sphereMeridianPowerLatexColorHex}{F_{$sphereMeridian}} &= \\frac{\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}}}{1-(VD)(\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}})} \\\\" +
            "&= \\frac{${sphereMeridianPower} \\space \\text{D} }{1-(0.012)(${sphereMeridianPower} \\space \\text{D} )} \\\\" +
            "&= \\frac{${sphereMeridianPower} \\space \\text{D} }{1-(${df.format(0.012*sphereMeridianPower.toFloat())} \\space \\text{D} )} \\\\" +
            "&= \\frac{${sphereMeridianPower} \\space \\text{D} }{${1-(df.format(0.012*sphereMeridianPower.toFloat()).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$sphereMeridianPowerLatexColorHex}{F_{$sphereMeridian}} = ${formatDiopter(power = clSphere.toFloat(), round = true, withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(clSphereMeridianPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(clSphereMeridianPowerLatex)
            }
        )
    }

    val clCylinderMeridianPowerLatex = "\$\$ \\begin{aligned}" +
            "\\textcolor{#$cylinderMeridianPowerLatexColorHex}{F_{$cylinderMeridian}} &= \\frac{\\textcolor{#$cylinderMeridianPowerLatexColorHex}{M_{$cylinderMeridian}}}{1-(VD)(\\textcolor{#$cylinderMeridianPowerLatexColorHex}{M_{$cylinderMeridian}})} \\\\" +
            "&= \\frac{${cylinderMeridianPower} \\space \\text{D} }{1-(0.012)(${cylinderMeridianPower} \\space \\text{D} )} \\\\" +
            "&= \\frac{${cylinderMeridianPower} \\space \\text{D} }{1-(${df.format(0.012*cylinderMeridianPower.toFloat())} \\space \\text{D} )} \\\\" +
            "&= \\frac{${cylinderMeridianPower} \\space \\text{D} }{${1-(df.format(0.012*cylinderMeridianPower.toFloat()).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clCylinder \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$cylinderMeridianPowerLatexColorHex}{F_{$cylinderMeridian}} = ${formatDiopter(power = clCylinder.toFloat(), round = true, withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(clCylinderMeridianPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(clCylinderMeridianPowerLatex)
            }
        )
    }

    Divider()

    Text(
        text = stringResource(id = AppStrings.tools_contact_lens_cl_optical_cross),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
    )

    val spherePowerLatex = "\$\$ \\begin{aligned} " +
            "Sph &= \\textcolor{#$sphereMeridianPowerLatexColorHex}{F_{$sphereMeridian}} \\\\" +
            "&= ${formatDiopter(power = clSphere.toFloat(), round = true, withSign = true)} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{Sph = ${formatDiopter(power = clSphere.toFloat(), round = true, withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(spherePowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(spherePowerLatex)
            }
        )
    }

    val cylinderPowerLatex = "\$\$ \\begin{aligned} " +
            "Cyl &= \\textcolor{#$cylinderMeridianPowerLatexColorHex}{F_{$cylinderMeridian}} - Sph \\\\" +
            "&= ${formatDiopter(power = clCylinder.toFloat(), round = true, withSign = true)} \\space \\text{D} - ${formatDiopter(power = clSphere.toFloat(), round = true, withSign = true)} \\space \\text{D} \\\\" +
            "&= ${df.format(formatDiopter(power = clCylinder.toFloat(), round = true, withSign = true).toFloat() - formatDiopter(power = clSphere.toFloat(), round = true, withSign = true).toFloat())} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{Cyl = ${formatDiopter(power = formatDiopter(power = clCylinder.toFloat(), round = true).toFloat() - formatDiopter(power = clSphere.toFloat(), round = true).toFloat(), round = true, withSign = true)} \\space \\text{D} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(cylinderPowerLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(cylinderPowerLatex)
            }
        )
    }

    Divider()

    Column {
        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_spectacle_prescription),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            text = "$spectacleSphere D Sph = $spectacleCylinder D Cyl x $spectacleAxis",
            style = MaterialTheme.typography.titleMedium
        )
    }

    Column {
        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_cl_prescription),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            text = "${formatDiopter(power = clSphere.toFloat(), round = true, withSign = true)} D Sph = ${formatDiopter(power = formatDiopter(power = clCylinder.toFloat(), round = true).toFloat() - formatDiopter(power = clSphere.toFloat(), round = true).toFloat(), round = true, withSign = true)} D Cyl x $spectacleAxis",
            style = MaterialTheme.typography.titleMedium
        )
    }
}