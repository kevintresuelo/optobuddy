package com.kevintresuelo.lorem.models.services.impl

import android.content.Context
import com.kevintresuelo.lorem.VisioHiltApp
import com.kevintresuelo.lorem.models.services.ContextService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContextServiceImpl @Inject constructor(@ApplicationContext private val app: Context) : ContextService {
    override fun getContext(): VisioHiltApp {
        return app as VisioHiltApp
    }

    override fun getApplicationContext(): Context {
        return getContext().applicationContext
    }
}