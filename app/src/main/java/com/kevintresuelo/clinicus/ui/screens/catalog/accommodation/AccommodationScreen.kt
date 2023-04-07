package com.kevintresuelo.clinicus.ui.screens.catalog.accommodation

import android.view.LayoutInflater
import androidx.compose.foundation.Image
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
import com.kevintresuelo.clinicus.components.buttons.SegmentedButton
import com.kevintresuelo.clinicus.ui.screens.catalog.contactlenspower.ContactLensPowerViewModel
import com.kevintresuelo.clinicus.utils.*
import kotlin.math.abs
import com.kevintresuelo.clinicus.R.drawable as AppDrawables
import com.kevintresuelo.clinicus.R.string as AppStrings

@Composable
fun AccommodationScreen(
    appState: ClinicusAppState,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactLensPowerViewModel = hiltViewModel()
) {

    appState.systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
    )

    appState.showTopAppBar(stringResource(id = AppStrings.tools_accommodation_title))

    val focusManager = LocalFocusManager.current
    val resources = LocalContext.current.resources

    var age by rememberSaveable { mutableStateOf("") }

    var showHofstetter by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = AppDrawables.headers_accommodation),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.77f)
                .clip(shape = CardDefaults.shape)
        )

        Text(
            text = stringResource(id = AppStrings.tools_accommodation_hofstetter),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        TextField(
            value = age,
            onValueChange = {
                age = getValidatedInteger(it)

                showHofstetter = age.isNotBlank() && age.toIntOrNull() != null
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = AppStrings.tools_accommodation_age)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        if(showHofstetter) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HofstetterAmplitude(age = age.toInt())
            }
        }

        Divider()

        val tabs = listOf(stringResource(id = AppStrings.tools_accommodation_tab_npa), stringResource(id = AppStrings.tools_accommodation_tab_aoa))
        var selectedTab by rememberSaveable { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxWidth()) {
            TabRow(
                selectedTabIndex = selectedTab
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
        }

        when(selectedTab) {
            0 -> {
                NearPointOfAccommodation()
            }
            1 -> {
                AmplitudeOfAccommodation()
            }
        }

    }
}

@Composable
fun HofstetterAmplitude(
    age: Int,
    modifier: Modifier = Modifier
) {
    val latexColor = MaterialTheme.colorScheme.onBackground.toArgb()

    val minAaLatex = "\$\$ \\begin{aligned}" +
            "AA_{Min} &= 15 - (0.25)(Age) \\\\" +
            "&= 15 - (0.25)($age) \\\\" +
            "&= 15 - ${(0.25*age).formatDecimal()} \\\\" +
            "&= ${(15-(0.25*age).formatDecimal().toFloat()).formatDecimal()} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{AA_{Min} = ${(15-(0.25*age).formatDecimal().toFloat()).formatDecimal()} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(minAaLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(minAaLatex)
            }
        )
    }

    val aveAaLatex = "\$\$ \\begin{aligned}" +
            "AA_{Ave} &= 18.5 - (0.3)(Age) \\\\" +
            "&= 18.5 - (0.3)($age) \\\\" +
            "&= 18.5 - ${(0.3*age).formatDecimal()} \\\\" +
            "&= ${(18.5-(0.3*age).formatDecimal().toFloat()).formatDecimal()} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{AA_{Ave} = ${(18.5-(0.3*age).formatDecimal().toFloat()).formatDecimal()} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(aveAaLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(aveAaLatex)
            }
        )
    }

    val maxAaLatex = "\$\$ \\begin{aligned}" +
            "AA_{Max} &= 25 - (0.4)(Age) \\\\" +
            "&= 25 - (0.4)($age) \\\\" +
            "&= 25 - ${(0.4*age).formatDecimal()} \\\\" +
            "&= ${(25-(0.4*age).formatDecimal().toFloat()).formatDecimal()} \\\\" +
            "\\end{aligned} \\\\" +
            "\\boxed{AA_{Max} = ${(25-(0.4*age).formatDecimal().toFloat()).formatDecimal()} } \$\$"

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_latex, null, false) as KatexView

                view.setText(maxAaLatex)
                view.setTextColor(latexColor)

                view
            },
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            update = {
                it.setText(maxAaLatex)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearPointOfAccommodation() {
    Text(
        text = stringResource(id = AppStrings.tools_accommodation_tab_npa_expanded),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    var distance by rememberSaveable { mutableStateOf("") }
    var showNpaSolution by rememberSaveable { mutableStateOf(false) }

    val unitOptions = listOf("in", "cm", "mm")
    var selectedUnitOption by remember { mutableStateOf(unitOptions[0]) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextField(
            value = distance,
            onValueChange = {
                distance = getValidatedDecimal(it)

                showNpaSolution = distance.isNotBlank() && distance.toFloatOrNull() != null
            },
            modifier = Modifier
                .weight(7f),
            label = { Text(text = stringResource(id = AppStrings.tools_accommodation_tab_npa_distance)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .weight(3f)
        ) {
            TextField(
                // The `menuAnchor` modifier must be passed to the text field for correctness.
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedUnitOption,
                onValueChange = {},
                label = { Text(stringResource(id = AppStrings.tools_accommodation_tab_npa_distance_unit)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                unitOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedUnitOption = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }

    val latexColor = MaterialTheme.colorScheme.onBackground.toArgb()

    if (showNpaSolution) {
        when (selectedUnitOption) {
            "in" -> {
                val npaInLatex = "\$\$ \\begin{aligned}" +
                        "P &= \\frac{40}{f_{in}} \\\\" +
                        "&= \\frac{40}{${distance.toFloat().formatDecimal()}} \\\\" +
                        "&= ${(40/distance.toFloat()).formatDecimal()} \\\\" +
                        "\\end{aligned} \\\\" +
                        "\\boxed{P = ${(40/distance.toFloat()).formatDecimal()} \\space \\text{D} } \$\$"

                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { context ->
                            val view = LayoutInflater.from(context)
                                .inflate(R.layout.layout_latex, null, false) as KatexView

                            view.setText(npaInLatex)
                            view.setTextColor(latexColor)

                            view
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        update = {
                            it.setText(npaInLatex)
                        }
                    )
                }
            }
            "cm" -> {
                val npaCmLatex = "\$\$ \\begin{aligned}" +
                        "P &= \\frac{100}{f_{cm}} \\\\" +
                        "&= \\frac{100}{${distance.toFloat().formatDecimal()}} \\\\" +
                        "&= ${(100/distance.toFloat()).formatDecimal()} \\\\" +
                        "\\end{aligned} \\\\" +
                        "\\boxed{P = ${(100/distance.toFloat()).formatDecimal()} \\space \\text{D} } \$\$"

                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { context ->
                            val view = LayoutInflater.from(context)
                                .inflate(R.layout.layout_latex, null, false) as KatexView

                            view.setText(npaCmLatex)
                            view.setTextColor(latexColor)

                            view
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        update = {
                            it.setText(npaCmLatex)
                        }
                    )
                }
            }
            "mm" -> {
                val npaMmLatex = "\$\$ \\begin{aligned}" +
                        "P &= \\frac{1000}{f_{mm} \\\\" +
                        "&= \\frac{1000}{${distance.toFloat().formatDecimal()}} \\\\" +
                        "&= ${(1000/distance.toFloat()).formatDecimal()} \\\\" +
                        "\\end{aligned} \\\\" +
                        "\\boxed{P = ${(1000/distance.toFloat()).formatDecimal()} \\space \\text{D} } \$\$"

                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { context ->
                            val view = LayoutInflater.from(context)
                                .inflate(R.layout.layout_latex, null, false) as KatexView

                            view.setText(npaMmLatex)
                            view.setTextColor(latexColor)

                            view
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        update = {
                            it.setText(npaMmLatex)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AmplitudeOfAccommodation() {
    Text(
        text = stringResource(id = AppStrings.tools_accommodation_tab_aoa_expanded),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    val focusManager = LocalFocusManager.current
    val resources = LocalContext.current.resources

    var showAoaComputation by rememberSaveable { mutableStateOf(false) }

    var distanceCorrection by rememberSaveable { mutableStateOf("") }
    var lensToBlur by rememberSaveable { mutableStateOf("") }

    val aoaCategories = listOf("Non-presbyope", "Presbyope")
    var selectedAoaCategory by rememberSaveable { mutableStateOf(0) }

    val latexColor = MaterialTheme.colorScheme.onBackground.toArgb()

    val (errorMessage, setErrorMessage) = rememberSaveable { mutableStateOf<String?>(null) }

    SegmentedButton(
        buttonTexts = aoaCategories
    ) {
        distanceCorrection = ""
        lensToBlur = ""
        showAoaComputation = false

        selectedAoaCategory = it
    }

    TextField(
        value = lensToBlur,
        onValueChange = {
            lensToBlur = getValidatedPower(it)
            showAoaComputation = false
        },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text = stringResource(id = AppStrings.tools_accommodation_tab_aoa_lens_to_blur)) },
        suffix = { Text(text = stringResource(id = AppStrings.generic_sph_unit)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )

    TextField(
        value = distanceCorrection,
        onValueChange = {
            distanceCorrection = getValidatedPower(it)
        },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text = stringResource(id = AppStrings.tools_accommodation_tab_aoa_distance_sph)) },
        suffix = { Text(text = stringResource(id = AppStrings.generic_sph_unit)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )

    Button(
        onClick = {
            var distanceCorrectionFloat = distanceCorrection.toFloatOrNull()
            var lensToBlurFloat = lensToBlur.toFloatOrNull()

            if (distanceCorrectionFloat == null || lensToBlurFloat == null) {
                setErrorMessage(resources.getString(AppStrings.tools_accommodation_tab_aoa_error_msg_invalid_power))
                showAoaComputation = false
                return@Button
            }

            if (selectedAoaCategory == 0 && lensToBlurFloat > distanceCorrectionFloat) {
                setErrorMessage(resources.getString(AppStrings.tools_accommodation_tab_aoa_error_msg_nonpresbyope_but_plus))
                showAoaComputation = false
                return@Button
            }

            if (selectedAoaCategory == 1 && lensToBlurFloat < distanceCorrectionFloat) {
                setErrorMessage(resources.getString(AppStrings.tools_accommodation_tab_aoa_error_msg_presbyope_but_minus))
                showAoaComputation = false
                return@Button
            }

            distanceCorrection = distanceCorrectionFloat.formatDiopter()
            lensToBlur = lensToBlurFloat.formatDiopter()

            showAoaComputation = true
            focusManager.clearFocus()
        },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    ) {
        Text(text = stringResource(id = AppStrings.tools_accommodation_tab_aoa_calculate))
    }

    if (showAoaComputation) {
        if (selectedAoaCategory == 0) {
            val tmaNonPresbyopeLatex = "\$\$ \\begin{aligned}" +
                    "TMA &= LIP - VT7 \\\\" +
                    "&= ${lensToBlur.formatDiopter()} \\space \\text{D} - ${distanceCorrection.formatDiopter()} \\space \\text{D} \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDecimal()} \\space \\text{D} \\\\" +
                    "\\end{aligned} \\\\" +
                    "\\boxed{TMA = ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDiopter()} \\space \\text{D} } \$\$"

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_latex, null, false) as KatexView

                        view.setText(tmaNonPresbyopeLatex)
                        view.setTextColor(latexColor)

                        view
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    update = {
                        it.setText(tmaNonPresbyopeLatex)
                    }
                )
            }

            val aoaNonPresbyopeLatex = "\$\$ \\begin{aligned}" +
                    "AOA &= TMA - WD \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDiopter()} \\space \\text{D} - 2.50 \\space \\text{D} \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat() - 2.50).formatDecimal()} \\space \\text{D} \\\\" +
                    "\\end{aligned} \\\\" +
                    "\\boxed{AOA = ${abs(lensToBlur.toFloat() - distanceCorrection.toFloat() - 2.50).formatDiopter()} \\space \\text{D} } \$\$"

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_latex, null, false) as KatexView

                        view.setText(aoaNonPresbyopeLatex)
                        view.setTextColor(latexColor)

                        view
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    update = {
                        it.setText(aoaNonPresbyopeLatex)
                    }
                )
            }
        } else {
            val tpaPresbyopeLatex = "\$\$ \\begin{aligned}" +
                    "TPA &= LIP - VT7 \\\\" +
                    "&= ${lensToBlur.formatDiopter()} \\space \\text{D} - ${distanceCorrection.formatDiopter()} \\space \\text{D} \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDecimal()} \\space \\text{D} \\\\" +
                    "\\end{aligned} \\\\" +
                    "\\boxed{TPA = ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDiopter()} \\space \\text{D} } \$\$"

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_latex, null, false) as KatexView

                        view.setText(tpaPresbyopeLatex)
                        view.setTextColor(latexColor)

                        view
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    update = {
                        it.setText(tpaPresbyopeLatex)
                    }
                )
            }

            val aoaPresbyopeLatex = "\$\$ \\begin{aligned}" +
                    "AOA &= TPA + WD \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat()).formatDiopter()} \\space \\text{D} + 2.50 \\space \\text{D} \\\\" +
                    "&= ${(lensToBlur.toFloat() - distanceCorrection.toFloat() + 2.50).formatDecimal()} \\space \\text{D} \\\\" +
                    "\\end{aligned} \\\\" +
                    "\\boxed{AOA = ${abs(lensToBlur.toFloat() - distanceCorrection.toFloat() + 2.50).formatDiopter()} \\space \\text{D} } \$\$"

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_latex, null, false) as KatexView

                        view.setText(aoaPresbyopeLatex)
                        view.setTextColor(latexColor)

                        view
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    update = {
                        it.setText(aoaPresbyopeLatex)
                    }
                )
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