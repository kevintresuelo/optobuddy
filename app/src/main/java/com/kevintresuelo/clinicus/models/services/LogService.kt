package com.kevintresuelo.lorem.models.services

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}