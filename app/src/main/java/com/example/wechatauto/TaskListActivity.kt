package com.example.wechatauto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.wechatauto.db.AppDatabase
import kotlinx.coroutines.*

class TaskListActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        
        db = AppDatabase.getInstance(this)
        loadTasks()
    }

    private fun loadTasks() {
        scope.launch {
            val tasks = db.scheduledTaskDao().getAllTasks()
            val taskDescriptions = tasks.map { task ->
                val group = db.savedGroupDao().getGroupById(task.groupId)
                "${group?.name ?: "未知群"} - ${task.scheduleTime} (${task.scheduleType})"
            }

            val listView = findViewById<ListView>(R.id.lv_tasks)
            val adapter = ArrayAdapter(
                this@TaskListActivity,
                android.R.layout.simple_list_item_1,
                taskDescriptions
            )
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val task = tasks[position]
                // TODO: 显示任务详情或编辑
            }

            listView.setOnItemLongClickListener { _, _, position, _ ->
                scope.launch {
                    db.scheduledTaskDao().delete(tasks[position])
                    loadTasks()
                }
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
