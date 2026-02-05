package com.example.wechatauto.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_groups")
data class SavedGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val wechatId: String,
    val avatar: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
