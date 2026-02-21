package com.example.wechatauto.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wechatauto.db.AppDatabase
import com.example.wechatauto.db.SendHistory
import com.example.wechatauto.accessibility.WechatAccessibilityService
import kotlinx.coroutines.*

class TaskExecutorReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "TaskExecutorReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId == -1) return
        
        val scope = CoroutineScope(Dispatchers.Main + Job())
        scope.launch {
            executeTask(context, taskId)
        }
    }

    private suspend fun executeTask(context: Context, taskId: Int) {
        val db = AppDatabase.getInstance(context)
        val task = db.scheduledTaskDao().getTaskById(taskId) ?: return
        val group = db.savedGroupDao().getGroupById(task.groupId) ?: return
        
        try {
            Log.d(TAG, "Executing task $taskId for group ${group.name}")
            
            val accessibilityService = WechatAccessibilityService.instance
            if (accessibilityService == null) {
                Log.e(TAG, "Accessibility service not available")
                recordFailure(db, task, group.id, "无障碍服务未启用")
                return
            }
            
            // 打开微信
            accessibilityService.openWechat()
            delay(2000) // 等待微信打开
            
            // 查找并点击群
            if (!accessibilityService.findAndClickByText(group.name)) {
                Log.e(TAG, "Failed to find group ${group.name}")
                recordFailure(db, task, group.id, "未找到群")
                return
            }
            delay(1000)
            
            // 输入文字
            if (!accessibilityService.inputText(task.text)) {
                Log.e(TAG, "Failed to input text")
                recordFailure(db, task, group.id, "输入文字失败")
                return
            }
            delay(500)
            
            // 发送
            if (!accessibilityService.performSend()) {
                Log.e(TAG, "Failed to send")
                recordFailure(db, task, group.id, "发送失败")
                return
            }
            
            // 记录成功
            recordSuccess(db, task, group.id)
            
            // 重新调度（如果是循环任务）
            if (task.scheduleType != "once") {
                val scheduler = TaskScheduler(context)
                scheduler.scheduleTask(task)
            } else {
                db.scheduledTaskDao().updateTaskStatus(taskId, "completed")
            }
            
            Log.d(TAG, "Task $taskId executed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error executing task $taskId", e)
            recordFailure(db, task, group.id, e.message ?: "未知错误")
        }
    }

    private suspend fun recordSuccess(db: AppDatabase, task: com.example.wechatauto.db.ScheduledTask, groupId: Int) {
        val history = SendHistory(
            taskId = task.id,
            groupId = groupId,
            status = "success"
        )
        db.sendHistoryDao().insert(history)
    }

    private suspend fun recordFailure(db: AppDatabase, task: com.example.wechatauto.db.ScheduledTask, groupId: Int, error: String) {
        val history = SendHistory(
            taskId = task.id,
            groupId = groupId,
            status = "failed",
            errorMessage = error
        )
        db.sendHistoryDao().insert(history)
    }
}
