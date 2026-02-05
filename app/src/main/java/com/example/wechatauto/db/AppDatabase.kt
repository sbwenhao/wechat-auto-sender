package com.example.wechatauto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedGroup::class, ScheduledTask::class, SendHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedGroupDao(): SavedGroupDao
    abstract fun scheduledTaskDao(): ScheduledTaskDao
    abstract fun sendHistoryDao(): SendHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wechat_auto_sender.db"
                ).build().also { instance = it }
            }
        }
    }
}
