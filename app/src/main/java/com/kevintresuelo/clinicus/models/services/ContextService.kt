package com.kevintresuelo.lorem.models.services

import android.content.Context
import com.kevintresuelo.lorem.VisioHiltApp

interface ContextService {
    fun getContext(): VisioHiltApp
    fun getApplicationContext(): Context
}