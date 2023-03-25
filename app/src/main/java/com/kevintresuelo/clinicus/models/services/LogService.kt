package com.kevintresuelo.clinicus.models.services

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}