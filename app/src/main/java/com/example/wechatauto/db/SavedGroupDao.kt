package com.example.wechatauto.db

import androidx.room.*

@Dao
interface SavedGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: SavedGroup): Long

    @Update
    suspend fun update(group: SavedGroup)

    @Delete
    suspend fun delete(group: SavedGroup)

    @Query("SELECT * FROM saved_groups ORDER BY createdAt DESC")
    suspend fun getAllGroups(): List<SavedGroup>

    @Query("SELECT * FROM saved_groups WHERE id = :id")
    suspend fun getGroupById(id: Int): SavedGroup?

    @Query("SELECT * FROM saved_groups WHERE wechatId = :wechatId")
    suspend fun getGroupByWechatId(wechatId: String): SavedGroup?

    @Query("DELETE FROM saved_groups WHERE id = :id")
    suspend fun deleteById(id: Int)
}
