package com.example.daggerhilt

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.daggerhilt.calculator.Calculator
import com.example.daggerhilt.car.Car
import com.example.daggerhilt.database.DatabaseInterface
import com.example.daggerhilt.logger.MyLogger
import com.example.daggerhilt.phone.Phone
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // コンストラクタインジェクション
    @Inject
    lateinit var calculator: Calculator

    // @Bindsを使用した注入
    @Inject
    lateinit var database: DatabaseInterface

    // @Providesを使用した注入
    @Inject
    lateinit var car: Car

    // アノテーションを利用して区別
    @Inject
    lateinit var phone: Phone

    // 事前定義された修飾子を利用
    @Inject
    lateinit var logger: MyLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "10+1=${calculator.add(1, 10)}", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "database: ${database.loadMessage()}", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "car: ${car.drive()}", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "phone: ${phone.batteryLevel()}", Toast.LENGTH_LONG).show()
        logger.log("Hello")
    }
}