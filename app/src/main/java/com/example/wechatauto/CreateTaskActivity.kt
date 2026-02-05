package com.example.wechatauto

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wechatauto.db.AppDatabase
import com.example.wechatauto.db.ScheduledTask
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var selectedGroupId: Int = -1
    private var selectedImagePath: String? = null
    private var selectedScheduleTime: String = ""
    private var selectedScheduleDate: String? = null
    private var selectedScheduleType: String = "daily"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        
        db = AppDatabase.getInstance(this)
        setupUI()
    }

    private fun setupUI() {
        val spinnerGroup = findViewById<Spinner>(R.id.spinner_group)
        val etText = findViewById<EditText>(R.id.et_send_text)
        val btnSelectImage = findViewById<Button>(R.id.btn_select_image)
        val spinnerScheduleType = findViewById<Spinner>(R.id.spinner_schedule_type)
        val btnSelectTime = findViewById<Button>(R.id.btn_select_time)
        val btnSaveTask = findViewById<Button>(R.id.btn_save_task)

        // 加载群列表
        scope.launch {
            val groups = db.savedGroupDao().getAllGroups()
            val groupNames = groups.map { it.name }
            val adapter = ArrayAdapter(
                this@CreateTaskActivity,
                android.R.layout.simple_spinner_item,
                groupNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGroup.adapter = adapter
            
            spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    selectedGroupId = groups[position].id
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // 定时类型
        val scheduleTypes = arrayOf("每天", "每周", "单次")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, scheduleTypes)
        spinnerScheduleType.adapter = typeAdapter
        spinnerScheduleType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedScheduleType = when (position) {
                    0 -> "daily"
                    1 -> "weekly"
                    else -> "once"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        btnSaveTask.setOnClickListener {
            val text = etText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入发送文字", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedGroupId == -1) {
                Toast.makeText(this, "请选择群", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedScheduleTime.isEmpty()) {
                Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            scope.launch {
                val task = ScheduledTask(
                    groupId = selectedGroupId,
                    text = text,
                    imagePath = selectedImagePath,
                    scheduleType = selectedScheduleType,
                    scheduleDate = selectedScheduleDate,
                    scheduleTime = selectedScheduleTime
                )
                db.scheduledTaskDao().insert(task)
                
                Toast.makeText(this@CreateTaskActivity, "任务已保存", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedScheduleTime = String.format("%02d:%02d", hourOfDay, minute)
                findViewById<Button>(R.id.btn_select_time).text = "时间: $selectedScheduleTime"
                
                if (selectedScheduleType == "once") {
                    showDatePickerDialog()
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedScheduleDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImagePath = data?.data?.toString()
            findViewById<Button>(R.id.btn_select_image).text = "已选择图片"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
