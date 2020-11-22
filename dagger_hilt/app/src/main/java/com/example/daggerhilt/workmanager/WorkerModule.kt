package com.example.daggerhilt.workmanager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object WorkerModule {
    @Provides
    fun provideWorkerDependency(): WorkerDependencyInterface {
        return WorkerDependency()
    }
}

interface WorkerDependencyInterface {
    val message: String
}

class WorkerDependency : WorkerDependencyInterface {
    override val message: String = "Hello From Worker"
}

