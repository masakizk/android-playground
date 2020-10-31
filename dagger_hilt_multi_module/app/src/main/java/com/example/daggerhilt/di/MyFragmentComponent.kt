package com.example.daggerhilt.di

import com.example.core.Router
import com.example.daggerhilt.ApplicationRouter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object MyFragmentComponent{
    @Provides
    fun provideRouter(): Router{
        return ApplicationRouter()
    }
}