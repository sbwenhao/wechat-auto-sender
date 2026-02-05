package com.example.wechatauto.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_tasks")
data class ScheduledTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val text: String,
    val imagePath: String? = null,
    val scheduleType: String, // "once", "daily", "weekly"
    val scheduleDate: String? = null, // YYYY-MM-DD for once
    val scheduleTime: String, // HH:mm
    val dayOfWeek: Int? = null, // 0-6 for weekly
    val status: String = "active", // "active", "paused", "completed"
    val createdAt: Long = System.currentTimeMillis(),
    val lastSentAt: Long? = null,
    val nextSendAt: Long? = null
)
