package com.example.wechatauto

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        setupUI()
    }

    private fun setupUI() {
        val tvAccessibilityStatus = findViewById<TextView>(R.id.tv_accessibility_status)
        val btnEnableAccessibility = findViewById<Button>(R.id.btn_enable_accessibility)
        val tvVersion = findViewById<TextView>(R.id.tv_version)

        // 检查无障碍服务状态
        updateAccessibilityStatus(tvAccessibilityStatus)

        btnEnableAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        tvVersion.text = "版本 1.0.0"
    }

    private fun updateAccessibilityStatus(tvStatus: TextView) {
        val enabled = isAccessibilityServiceEnabled()
        tvStatus.text = if (enabled) "✓ 已启用" else "✗ 未启用"
        tvStatus.setTextColor(if (enabled) android.graphics.Color.GREEN else android.graphics.Color.RED)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = getSystemService(android.content.Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        return enabledServices.contains("com.example.wechatauto/.accessibility.WechatAccessibilityService")
    }

    override fun onResume() {
        super.onResume()
        val tvAccessibilityStatus = findViewById<TextView>(R.id.tv_accessibility_status)
        updateAccessibilityStatus(tvAccessibilityStatus)
    }
}
