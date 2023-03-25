package com.kevintresuelo.lorem.models.services.modules

import com.kevintresuelo.lorem.models.services.ConfigurationService
import com.kevintresuelo.lorem.models.services.ContextService
import com.kevintresuelo.lorem.models.services.LogService
import com.kevintresuelo.lorem.models.services.StorageService
import com.kevintresuelo.clinicus.models.services.impl.ConfigurationServiceImpl
import com.kevintresuelo.lorem.models.services.impl.ContextServiceImpl
import com.kevintresuelo.lorem.models.services.impl.LogServiceImpl
import com.kevintresuelo.lorem.models.services.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds
    abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService

    @Binds
    abstract fun provideContextService(impl: ContextServiceImpl): ContextService

}