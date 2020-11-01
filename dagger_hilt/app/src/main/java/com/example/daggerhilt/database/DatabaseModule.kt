package com.example.daggerhilt.database

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class DatabaseModule {
    // インターフェースなどコンストラクタインジェクションが
    // できないクラスに対して依存注入をする.
    // ただし、注入されるもの(AppleDatabase)は
    // コンストラクタインジェクションを定義しないといけない
    @Binds
    abstract fun bindDatabase(database: AppleDatabase): DatabaseInterface
}