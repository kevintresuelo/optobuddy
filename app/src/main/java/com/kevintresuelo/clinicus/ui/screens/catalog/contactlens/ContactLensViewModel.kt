package com.kevintresuelo.clinicus.ui.screens.catalog.contactlens

import com.kevintresuelo.clinicus.models.services.ContextService
import com.kevintresuelo.clinicus.models.services.LogService
import com.kevintresuelo.clinicus.models.services.StorageService
import com.kevintresuelo.clinicus.ui.screens.ClinicusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactLensViewModel @Inject constructor(
    private val storageService: StorageService,
    private val contextService: ContextService,
    logService: LogService,
) : ClinicusViewModel(logService) {

    init {
    }

}