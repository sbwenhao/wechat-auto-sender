package com.example.wechatauto.db

import androidx.room.*

@Dao
interface SendHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SendHistory): Long

    @Query("SELECT * FROM send_history WHERE taskId = :taskId ORDER BY sentAt DESC")
    suspend fun getHistoryByTaskId(taskId: Int): List<SendHistory>

    @Query("SELECT * FROM send_history WHERE groupId = :groupId ORDER BY sentAt DESC")
    suspend fun getHistoryByGroupId(groupId: Int): List<SendHistory>

    @Query("SELECT * FROM send_history ORDER BY sentAt DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int = 100): List<SendHistory>

    @Query("DELETE FROM send_history WHERE sentAt < :beforeTime")
    suspend fun deleteOldHistory(beforeTime: Long)
}
