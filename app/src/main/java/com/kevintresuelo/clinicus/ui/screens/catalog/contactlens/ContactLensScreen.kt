package com.kevintresuelo.clinicus.ui.screens.catalog.contactlens

import android.view.LayoutInflater
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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
import com.kevintresuelo.clinicus.R.drawable as AppDrawables

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
        Image(
            painter = painterResource(id = AppDrawables.headers_contact_lens),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.77f)
                .clip(shape = CardDefaults.shape)
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

                spectacleSphere = spectacleSphereFloat.formatDiopter()

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
                    spectacleCylinder = spectacleCylinderFloat.formatDiopter()
                    spectacleAxis = spectacleAxisInt.toString()
                }

                showCalculation = true

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
                        spectacleSphere = spectacleSphere.toFloat()
                    )
                } else {
                    SpherocylinderContactLensPower(
                        spectacleSphere = spectacleSphere.toFloat(),
                        spectacleCylinder = spectacleCylinder.toFloat(),
                        spectacleAxis = spectacleAxis.toInt(),
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
    spectacleSphere: Float,
    modifier: Modifier = Modifier
) {
    val spherePower = spectacleSphere.formatDecimal()

    val clSphere = (spectacleSphere/(1-((0.012*spectacleSphere).formatDecimal().toFloat()))).formatDecimal()

    val clPowerLatex = "\$\$ \\begin{aligned}" +
            "F &= \\frac{SR}{1-(VD)(SR)} \\\\" +
            "&= \\frac{ ${spherePower.formatDiopter()} \\space \\text{D} }{1-(0.012)( ${spherePower.formatDiopter()} \\space \\text{D} )} \\\\" +
            "&= \\frac{ ${spherePower.formatDiopter()} \\space \\text{D} }{1-(${(0.012*spherePower.toFloat()).formatDecimal()}  )} \\\\" +
            "&= \\frac{ $spherePower \\space \\text{D} }{${1-((0.012*spherePower.toFloat()).formatDecimal()).toFloat()} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{F = ${clSphere.formatDiopter()} \\space \\text{D} } \$\$"

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



    Divider()

    Column {
        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_spectacle_prescription),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            text = "${spherePower.formatDiopter(round = true, withSign = true)} D Sph",
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
            text = "${clSphere.formatDiopter(round = true, withSign = true)} D Sph",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SpherocylinderContactLensPower(
    spectacleSphere: Float,
    spectacleCylinder: Float,
    spectacleAxis: Int,
    modifier: Modifier = Modifier
) {
    val clSphere = (spectacleSphere/(1-((0.012*spectacleSphere).formatDecimal().toFloat()))).formatDecimal()
    val clCylinder = ((spectacleSphere+spectacleCylinder)/(1-((0.012*(spectacleSphere+spectacleCylinder)).formatDecimal().toFloat()))).formatDecimal()

    val sphereMeridianPower = spectacleSphere
    val cylinderMeridianPower = spectacleSphere + spectacleCylinder

    val sphereMeridian = spectacleAxis
    val cylinderMeridian = if (sphereMeridian < 90) sphereMeridian + 90 else sphereMeridian - 90

    var showSphericalEquivalent by rememberSaveable { mutableStateOf(false) }

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
            "&= ${spectacleSphere.formatDiopter()} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}} = ${spectacleSphere.formatDiopter(withSign = true)} \\space \\text{D} } \$\$"

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
            "&= ${spectacleSphere.formatDiopter()} \\space \\text{D} + ${spectacleCylinder.formatDiopter()} \\space \\text{D} \\\\" +
            "&= ${(spectacleSphere + spectacleCylinder).formatDiopter()} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$cylinderMeridianPowerLatexColorHex}{M_{$cylinderMeridian}} = ${(spectacleSphere + spectacleCylinder).formatDiopter(withSign = true)} \\space \\text{D} } \$\$"

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
    val df = DecimalFormat("#.#####")

    val clSphereMeridianPowerLatex = "\$\$ \\begin{aligned}" +
            "\\textcolor{#$sphereMeridianPowerLatexColorHex}{F_{$sphereMeridian}} &= \\frac{\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}}}{1-(VD)(\\textcolor{#$sphereMeridianPowerLatexColorHex}{M_{$sphereMeridian}})} \\\\" +
            "&= \\frac{${sphereMeridianPower.formatDiopter()} \\space \\text{D} }{1-(0.012)(${sphereMeridianPower.formatDiopter()} \\space \\text{D} )} \\\\" +
            "&= \\frac{${sphereMeridianPower.formatDiopter()} \\space \\text{D} }{1-(${df.format(0.012*sphereMeridianPower)} \\space \\text{D} )} \\\\" +
            "&= \\frac{${sphereMeridianPower.formatDiopter()} \\space \\text{D} }{${1-(df.format(0.012*sphereMeridianPower).toFloat())} \\space \\text{D} } \\\\" +
            "&= $clSphere \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$sphereMeridianPowerLatexColorHex}{F_{$sphereMeridian}} = ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} } \$\$"

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
            "&= \\frac{${cylinderMeridianPower.formatDiopter()} \\space \\text{D} }{1-(0.012)(${cylinderMeridianPower.formatDiopter()} \\space \\text{D} )} \\\\" +
            "&= \\frac{${cylinderMeridianPower.formatDiopter()} \\space \\text{D} }{1-(${(0.012*cylinderMeridianPower).formatDecimal()} \\space \\text{D} )} \\\\" +
            "&= \\frac{${cylinderMeridianPower.formatDiopter()} \\space \\text{D} }{${1-((0.012*cylinderMeridianPower).formatDecimal().toFloat())} \\space \\text{D} } \\\\" +
            "&= $clCylinder \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{\\textcolor{#$cylinderMeridianPowerLatexColorHex}{F_{$cylinderMeridian}} = ${clCylinder.formatDiopter(round = true, withSign = true)} \\space \\text{D} } \$\$"

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
            "&= ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{Sph = ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} } \$\$"

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
            "&= ${clCylinder.formatDiopter(round = true, withSign = true)} \\space \\text{D} - ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} \\\\" +
            "&= ${(clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)} \\space \\text{D} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{Cyl = ${(clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)} \\space \\text{D} } \$\$"

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
            text = "${spectacleSphere.formatDiopter(withSign = true)} D Sph = ${spectacleCylinder.formatDiopter(withSign = true)} D Cyl x $spectacleAxis",
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
            text = "${clSphere.formatDiopter(round = true, withSign = true)} D Sph = ${(clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)} D Cyl x $spectacleAxis",
            style = MaterialTheme.typography.titleMedium
        )
    }

    if (showSphericalEquivalent) {
        Divider()

        Text(
            text = stringResource(id = AppStrings.tools_contact_lens_spherical_equivalent),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        val sphericalEquivalentLatex = "\$\$ \\begin{aligned} " +
                "SE &= Sph + \\frac{Cyl}{2} \\\\" +
                "&= ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} + \\frac{ ${(clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)} \\space \\text{D} }{2} \\\\" +
                "&= ${clSphere.formatDiopter(round = true, withSign = true)} \\space \\text{D} + ${(((clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)).toFloat()/2).formatDecimal()} \\space \\text{D} \\\\" +
                "&= ${(clSphere.formatDiopter(round = true, withSign = true).toFloat() + ((clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)).toFloat()/2).formatDecimal()} \\space \\text{D} \\\\" +
                "\\end{aligned} \\\\" +
                "\\boxed{SE = ${(clSphere.formatDiopter(round = true, withSign = true).toFloat() + ((clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)).toFloat()/2).formatDiopter(round = true, withSign = true)} \\space \\text{D} } \$\$"

        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.layout_latex, null, false) as KatexView

                    view.setText(sphericalEquivalentLatex)
                    view.setTextColor(latexColor)

                    view
                },
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
                update = {
                    it.setText(sphericalEquivalentLatex)
                }
            )
        }

        Column {
            Text(
                text = stringResource(id = AppStrings.tools_contact_lens_se_prescription),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )

            Text(
                text = "${(clSphere.formatDiopter(round = true, withSign = true).toFloat() + ((clCylinder.formatDiopter(round = true, withSign = true).toFloat() - clSphere.formatDiopter(round = true, withSign = true).toFloat()).formatDiopter(round = true, withSign = true)).toFloat()/2).formatDiopter(round = true, withSign = true)} D Sph",
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        Button(
            onClick = {
                showSphericalEquivalent = true
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text(text = stringResource(id = AppStrings.tools_contact_lens_spherical_equivalent_calculate))
        }
    }
}