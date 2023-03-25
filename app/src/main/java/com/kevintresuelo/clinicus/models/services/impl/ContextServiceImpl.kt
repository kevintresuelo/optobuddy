package com.kevintresuelo.clinicus.models.services.impl

import android.content.Context
import com.kevintresuelo.clinicus.ClinicusHiltApp
import com.kevintresuelo.clinicus.models.services.ContextService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContextServiceImpl @Inject constructor(@ApplicationContext private val app: Context) :
    ContextService {
    override fun getContext(): ClinicusHiltApp {
        return app as ClinicusHiltApp
    }

    override fun getApplicationContext(): Context {
        return getContext().applicationContext
    }
}