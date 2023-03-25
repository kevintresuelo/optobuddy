package com.kevintresuelo.clinicus.utils.annotations

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Phone Portrait",
    group = "Portrait Devices",
    device = "spec:width=411dp,height=891dp"
)
annotation class PhonePortraitPreview

@Preview(
    name = "Foldable Portrait",
    group = "Portrait Devices",
    device = "spec:width=673.5dp,height=841dp,dpi=480"
)
annotation class FoldablePortraitPreview

@Preview(
    name = "Tablet Portrait",
    group = "Portrait Devices",
    device = "spec:width=800dp,height=1280dp,dpi=480"
)
annotation class TabletPortraitPreview

@Preview(
    name = "Phone Landscape",
    group = "Landscape Devices",
    device = "spec:width=891dp,height=411dp"
)
annotation class PhoneLandscapePreview

@Preview(
    name = "Foldable Landscape",
    group = "Landscape Devices",
    device = "spec:width=841dp,height=673.5dp,dpi=480"
)
annotation class FoldableLandscapePreview

@Preview(
    name = "Tablet Landscape",
    group = "Landscape Devices",
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
annotation class TabletLandscapePreview

@Preview(
    name = "Desktop Landscape",
    group = "Landscape Devices",
    device = "spec:width=1920dp,height=1080dp,dpi=480"
)
annotation class DesktopLandscapePreview

@PhonePortraitPreview
@FoldablePortraitPreview
@TabletPortraitPreview
annotation class PortraitDevicesPreview

@PhoneLandscapePreview
@FoldableLandscapePreview
@TabletLandscapePreview
@DesktopLandscapePreview
annotation class LandscapeDevicesPreview

@PortraitDevicesPreview
@LandscapeDevicesPreview
annotation class DevicesPreview