package com.example.daggerhilt.phone

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
object PhoneModule {
    // 同じ型に複数のバインディングを提供したいときは
    // アノテーションを付けることによって区別をする

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BatteryL

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BatteryS

    @Provides
    @BatteryL
    fun provideLargeBattery(): Battery {
        return LargeBattery()
    }

    @Provides
    @BatteryS
    fun provideSmallBattery(): Battery {
        return SmallBattery()
    }

    @Provides
    fun providePhone(
            // アノテーションにより指定
            @BatteryL battery: Battery
    ): Phone {
        return Phone(battery)
    }
}