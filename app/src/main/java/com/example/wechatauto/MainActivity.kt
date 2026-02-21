package com.example.wechatauto

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wechatauto.db.AppDatabase
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        db = AppDatabase.getInstance(this)
        
        setupUI()
    }

    private fun setupUI() {
        val btnSelectGroup = findViewById<Button>(R.id.btn_select_group)
        val btnNewTask = findViewById<Button>(R.id.btn_new_task)
        val btnTasks = findViewById<Button>(R.id.btn_tasks)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        val tvGroupCount = findViewById<TextView>(R.id.tv_group_count)
        val tvTaskCount = findViewById<TextView>(R.id.tv_task_count)

        btnSelectGroup.setOnClickListener {
            startActivity(Intent(this, GroupSelectorActivity::class.java))
        }

        btnNewTask.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }

        btnTasks.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // 更新统计信息
        updateStats(tvGroupCount, tvTaskCount)
    }

    private fun updateStats(tvGroupCount: TextView, tvTaskCount: TextView) {
        scope.launch {
            val groupCount = db.savedGroupDao().getAllGroups().size
            val taskCount = db.scheduledTaskDao().getAllTasks().size
            
            tvGroupCount.text = "已保存的群: $groupCount"
            tvTaskCount.text = "定时任务: $taskCount"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
