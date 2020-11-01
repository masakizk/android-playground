package com.example.daggerhilt.car

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object CarModule {

    // Provideを用いると、
    // コンストラクタインジェクションができないクラスも注入できる
    @Provides
    fun provideEngine(): Engine {
        return HondaEngine()
    }

    // この型の依存を指定できる(自動で依存注入してくれる)
    @Provides
    fun provideCar(
            engine: Engine
    ): Car{
        return HondaCar(engine)
    }
}