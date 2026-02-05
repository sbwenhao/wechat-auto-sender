package com.example.wechatauto.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "send_history")
data class SendHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val groupId: Int,
    val sentAt: Long = System.currentTimeMillis(),
    val status: String, // "success", "failed"
    val errorMessage: String? = null
)
