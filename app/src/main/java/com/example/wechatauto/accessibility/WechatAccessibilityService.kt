package com.example.wechatauto.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Handler
import android.os.Looper
import android.util.Log

class WechatAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "WechatAccessibility"
        private const val WECHAT_PACKAGE = "com.tencent.mm"
        var instance: WechatAccessibilityService? = null
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isProcessing = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "Accessibility Service Connected")
        
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                handleViewClicked(event)
            }
        }
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString()
        Log.d(TAG, "Window changed: $packageName")
    }

    private fun handleViewClicked(event: AccessibilityEvent) {
        Log.d(TAG, "View clicked: ${event.text}")
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "Accessibility Service Destroyed")
    }

    /**
     * 打开微信应用
     */
    fun openWechat() {
        val intent = packageManager.getLaunchIntentForPackage(WECHAT_PACKAGE)
        if (intent != null) {
            startActivity(intent)
        }
    }

    /**
     * 查找并点击指定文本的元素
     */
    fun findAndClickByText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return findAndClickNode(rootNode, text)
    }

    private fun findAndClickNode(node: AccessibilityNodeInfo, text: String): Boolean {
        if (node.text?.toString()?.contains(text) == true && node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndClickNode(child, text)) {
                return true
            }
        }
        return false
    }

    /**
     * 在输入框中输入文本
     */
    fun inputText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return findAndInputText(rootNode, text)
    }

    private fun findAndInputText(node: AccessibilityNodeInfo, text: String): Boolean {
        if (node.isEditable && node.className?.contains("EditText") == true) {
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            val arguments = android.os.Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndInputText(child, text)) {
                return true
            }
        }
        return false
    }

    /**
     * 执行发送操作
     */
    fun performSend(): Boolean {
        return findAndClickByText("发送") || findAndClickByText("Send")
    }
}
