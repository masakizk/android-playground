package com.example.daggerhilt.fruits

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

// HiltによってFruitsを提供する
@Module
@InstallIn(ApplicationComponent::class)
object FruitModule {
    @Provides
    fun provideFruit(): Fruits = Apple()
}