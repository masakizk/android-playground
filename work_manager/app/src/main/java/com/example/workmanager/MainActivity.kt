package com.example.workmanager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.workmanager.databinding.ActivityMainBinding
import com.example.workmanager.worker.EncryptWorker
import com.example.workmanager.worker.LongTimeWorker
import com.example.workmanager.worker.SendMessageWorker
import com.example.workmanager.worker.SendMessagesWorker
import com.example.workmanager.workrequest.DataWorkRequest
import com.example.workmanager.workrequest.ScheduleWorkRequest

class MainActivity : AppCompatActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(applicationContext)

        binding.apply {
            createWork.setOnClickListener { createWork() }
            createUniqueWork.setOnClickListener { createUniqueWork() }
            createWorkChain.setOnClickListener { createWorkChain() }
            createWorkChainParallel.setOnClickListener { createWorkChainParallel() }
            createLongTimeWorker.setOnClickListener { createLongTimeWorker() }
        }
    }

    private fun createWork() {
        log("create work")

        // WorkとWorkRequestを定義し、
        // 処理をキューに登録する
        workManager.enqueue(DataWorkRequest.createWorkRequest())
    }

    private fun createUniqueWork() {
        val request = ScheduleWorkRequest.createOneTimeWork()
        workManager
            .enqueueUniqueWork(
                "sendMessage",
                ExistingWorkPolicy.KEEP,
                request
            )

        val info = workManager.getWorkInfosForUniqueWorkLiveData("sendMessage")
        info.observe(this) { workInfos ->
            log(workInfos.map { "id: ${it.id}, progress: ${it.progress}, state: ${it.state}\n" }
                .toString())
        }
    }

    private fun createWorkChain() {
        val encrypt = OneTimeWorkRequestBuilder<EncryptWorker>()
            .setInputData(workDataOf("MESSAGE" to "Hello"))
            .build()

        val send = OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setInputMerger(ArrayCreatingInputMerger::class.java)
            .setInputData(workDataOf("SENDER" to "Alice"))
            .build()

        workManager
            .beginWith(encrypt)
            .then(send)
            .enqueue()
    }

    private fun createWorkChainParallel() {
        val encryptApple = OneTimeWorkRequestBuilder<EncryptWorker>()
            .setInputData(workDataOf("MESSAGE" to "Apple"))
            .build()

        val encryptBanana = OneTimeWorkRequestBuilder<EncryptWorker>()
            .setInputData(workDataOf("MESSAGE" to "Banana"))
            .build()

        // 複数のメッセージを送信
        val sendMessages = OneTimeWorkRequestBuilder<SendMessagesWorker>()
            .setInputMerger(ArrayCreatingInputMerger::class.java)
            .build()

        workManager
            .beginWith(listOf(encryptApple, encryptBanana))
            .then(sendMessages)
            .enqueue()

        // 単一のメッセージを送信
        val sendMessage = OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setInputMerger(OverwritingInputMerger::class.java)
            .setInputData(workDataOf("SENDER" to "ALICE"))
            .build()

        workManager
            .beginWith(listOf(encryptApple, encryptBanana))
            .then(sendMessage)
            .enqueue()
    }

    private fun createLongTimeWorker() {
        val worker = OneTimeWorkRequestBuilder<LongTimeWorker>().build()
        workManager.enqueue(worker)
    }


    private fun log(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}