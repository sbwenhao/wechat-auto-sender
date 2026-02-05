package com.example.wechatauto.db

import androidx.room.*

@Dao
interface ScheduledTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: ScheduledTask): Long

    @Update
    suspend fun update(task: ScheduledTask)

    @Delete
    suspend fun delete(task: ScheduledTask)

    @Query("SELECT * FROM scheduled_tasks ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<ScheduledTask>

    @Query("SELECT * FROM scheduled_tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): ScheduledTask?

    @Query("SELECT * FROM scheduled_tasks WHERE groupId = :groupId")
    suspend fun getTasksByGroupId(groupId: Int): List<ScheduledTask>

    @Query("SELECT * FROM scheduled_tasks WHERE status = 'active' ORDER BY nextSendAt ASC")
    suspend fun getActiveTasks(): List<ScheduledTask>

    @Query("UPDATE scheduled_tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, status: String)

    @Query("UPDATE scheduled_tasks SET lastSentAt = :time, nextSendAt = :nextTime WHERE id = :id")
    suspend fun updateTaskSentTime(id: Int, time: Long, nextTime: Long)

    @Query("DELETE FROM scheduled_tasks WHERE id = :id")
    suspend fun deleteById(id: Int)
}
