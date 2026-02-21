package com.example.wechatauto.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.wechatauto.db.AppDatabase
import kotlinx.coroutines.*

class TaskSchedulerService : Service() {
    companion object {
        private const val TAG = "TaskSchedulerService"
    }

    private lateinit var scheduler: TaskScheduler
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")
        scheduler = TaskScheduler(this)
        
        // 定期检查并重新调度任务
        scope.launch {
            while (isActive) {
                scheduler.rescheduleAllTasks()
                delay(60000) // 每分钟检查一次
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service Started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service Destroyed")
        scheduler.destroy()
        scope.cancel()
    }
}
