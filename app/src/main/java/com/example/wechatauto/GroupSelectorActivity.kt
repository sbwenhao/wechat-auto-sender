package com.example.wechatauto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wechatauto.db.AppDatabase
import com.example.wechatauto.db.SavedGroup
import kotlinx.coroutines.*

class GroupSelectorActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selector)
        
        db = AppDatabase.getInstance(this)
        
        setupUI()
    }

    private fun setupUI() {
        val etGroupName = findViewById<EditText>(R.id.et_group_name)
        val btnOpenWechat = findViewById<Button>(R.id.btn_open_wechat)
        val btnSaveGroup = findViewById<Button>(R.id.btn_save_group)

        btnOpenWechat.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "未安装微信", Toast.LENGTH_SHORT).show()
            }
        }

        btnSaveGroup.setOnClickListener {
            val groupName = etGroupName.text.toString().trim()
            if (groupName.isEmpty()) {
                Toast.makeText(this, "请输入群名称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            scope.launch {
                val group = SavedGroup(
                    name = groupName,
                    wechatId = "wechat_${System.currentTimeMillis()}"
                )
                db.savedGroupDao().insert(group)
                
                Toast.makeText(this@GroupSelectorActivity, "群已保存", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
