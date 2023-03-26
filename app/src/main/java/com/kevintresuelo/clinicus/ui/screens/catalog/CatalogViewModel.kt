package com.kevintresuelo.clinicus.ui.screens.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kevintresuelo.clinicus.models.services.ContextService
import com.kevintresuelo.clinicus.models.services.LogService
import com.kevintresuelo.clinicus.models.services.StorageService
import com.kevintresuelo.clinicus.ui.screens.ClinicusViewModel
import com.kevintresuelo.clinicus.utils.DataStore
import com.kevintresuelo.clinicus.utils.readString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val storageService: StorageService,
    private val contextService: ContextService,
    logService: LogService,
) : ClinicusViewModel(logService) {

    init {
    }

}