package com.example.daggerhilt.assistedinject

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@AssistedModule
@InstallIn(ActivityComponent::class)
object AssistedInjectModule