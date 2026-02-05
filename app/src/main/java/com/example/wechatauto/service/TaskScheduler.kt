package com.example.wechatauto.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wechatauto.db.AppDatabase
import com.example.wechatauto.db.ScheduledTask
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class TaskScheduler(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        private const val TAG = "TaskScheduler"
    }

    fun scheduleTask(task: ScheduledTask) {
        scope.launch {
            val nextTime = calculateNextSendTime(task)
            if (nextTime > 0) {
                val updatedTask = task.copy(nextSendAt = nextTime)
                db.scheduledTaskDao().update(updatedTask)
                
                val intent = Intent(context, TaskExecutorReceiver::class.java).apply {
                    putExtra("taskId", task.id)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTime,
                        pendingIntent
                    )
                    Log.d(TAG, "Task ${task.id} scheduled for ${Date(nextTime)}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to schedule task ${task.id}", e)
                }
            }
        }
    }

    fun cancelTask(taskId: Int) {
        val intent = Intent(context, TaskExecutorReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Task $taskId cancelled")
    }

    private fun calculateNextSendTime(task: ScheduledTask): Long {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val now = Calendar.getInstance()
        val nextTime = Calendar.getInstance()
        
        try {
            val time = timeFormat.parse(task.scheduleTime) ?: return -1
            nextTime.apply {
                set(Calendar.HOUR_OF_DAY, time.hours)
                set(Calendar.MINUTE, time.minutes)
                set(Calendar.SECOND, 0)
            }
            
            when (task.scheduleType) {
                "once" -> {
                    if (task.scheduleDate != null) {
                        val date = dateFormat.parse(task.scheduleDate) ?: return -1
                        nextTime.apply {
                            set(Calendar.YEAR, date.year + 1900)
                            set(Calendar.MONTH, date.month)
                            set(Calendar.DAY_OF_MONTH, date.date)
                        }
                    }
                    if (nextTime.before(now)) {
                        return -1 // 时间已过
                    }
                }
                "daily" -> {
                    if (nextTime.before(now)) {
                        nextTime.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                "weekly" -> {
                    val targetDayOfWeek = (task.dayOfWeek ?: 0) + 1
                    while (nextTime.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek) {
                        nextTime.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    if (nextTime.before(now)) {
                        nextTime.add(Calendar.WEEK_OF_MONTH, 1)
                    }
                }
            }
            
            return nextTime.timeInMillis
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating next send time", e)
            return -1
        }
    }

    fun rescheduleAllTasks() {
        scope.launch {
            val tasks = db.scheduledTaskDao().getActiveTasks()
            tasks.forEach { task ->
                scheduleTask(task)
            }
        }
    }

    fun destroy() {
        scope.cancel()
    }
}
