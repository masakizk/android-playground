package com.example.daggerhilt.fruits

import android.content.Context
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/*
    ほとんどのエントリーポイントはAndroidでインスタンス化された場所にある(Fragment, Activity)
    そしてこれらはAndroidEntryPointで指定することによって、DIされる.
    その他のクラスで必要なときにはEntryPointによって指定する
 */
class FruitsApplication: NonHiltLibraryClass {
    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface FruitsApplicationInterface{
        // 非Hiltアプリケーションにおいて、
        // Hiltの提供するクラスを利用したいとき
        // EntryPointを利用して取得する
        // -> HiltでDIされたインスタンスがこのクラスで利用可能になる
        //    (FruitsModuleで指定されたFruitsを利用できる)
        fun getFruits(): Fruits
    }

    companion object{
        private const val TAG = "FruitsApplication"
    }

    fun showFruits(context: Context){
        val fruitsAppInterface = EntryPoints.get(context, FruitsApplicationInterface::class.java)
        val fruits = fruitsAppInterface.getFruits()
        Log.d(TAG, "showFruits: ${fruits.name}")
    }
}