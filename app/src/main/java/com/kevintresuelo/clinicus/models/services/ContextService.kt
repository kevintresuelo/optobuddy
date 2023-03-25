package com.kevintresuelo.clinicus.models.services

import android.content.Context
import com.kevintresuelo.clinicus.ClinicusHiltApp

interface ContextService {
    fun getContext(): ClinicusHiltApp
    fun getApplicationContext(): Context
}