# 微信定时发送助手

一个强大的 Android 应用，支持定时自动向微信群发送文字和图片。

## 功能特性

✨ **核心功能**
- 🎯 手动选择微信群并自动记住
- 🕒 支持定时发送（单次、每天、每周）
- 🖼️ 支持发送图片和文字
- 🤖 使用无障碍服务自动化操作
- 📊 发送历史记录和统计

## 系统要求

- Android 7.0 (API 24) 或更高版本
- 已安装微信应用
- 已启用无障碍服务权限

## 安装步骤

1. **下载 APK 文件**
   - 从发布页面下载最新的 APK 文件

2. **安装应用**
   - 在 Android 设备上打开 APK 文件
   - 按照提示完成安装

3. **启用无障碍服务**
   - 打开应用后，进入"设置"
   - 点击"启用无障碍服务"
   - 在系统设置中找到"微信定时发送"
   - 启用无障碍服务权限

## 使用指南

### 第一步：选择微信群

1. 点击首页的"选择微信群"按钮
2. 点击"打开微信"打开微信应用
3. 在微信中选择要发送的群
4. 返回应用，输入群名称
5. 点击"保存群"

### 第二步：创建定时任务

1. 点击首页的"新建任务"按钮
2. 选择要发送的群
3. 输入要发送的文字
4. （可选）选择要发送的图片
5. 选择定时类型（每天/每周/单次）
6. 选择发送时间
7. 点击"保存任务"

### 第三步：管理任务

1. 点击首页的"任务列表"查看所有任务
2. 长按任务可删除
3. 在"设置"中可查看无障碍服务状态

## 技术架构

- **数据库**：Room Database（SQLite）
- **定时任务**：AlarmManager + BroadcastReceiver
- **自动化**：Android AccessibilityService
- **异步处理**：Kotlin Coroutines

## 文件结构

```
app/src/main/
├── java/com/example/wechatauto/
│   ├── MainActivity.kt              # 主界面
│   ├── GroupSelectorActivity.kt     # 群选择
│   ├── CreateTaskActivity.kt        # 任务创建
│   ├── TaskListActivity.kt          # 任务列表
│   ├── SettingsActivity.kt          # 设置
│   ├── db/                          # 数据库相关
│   │   ├── AppDatabase.kt
│   │   ├── SavedGroup.kt
│   │   ├── ScheduledTask.kt
│   │   ├── SendHistory.kt
│   │   ├── SavedGroupDao.kt
│   │   ├── ScheduledTaskDao.kt
│   │   └── SendHistoryDao.kt
│   ├── accessibility/               # 无障碍服务
│   │   └── WechatAccessibilityService.kt
│   └── service/                     # 后台服务
│       ├── TaskScheduler.kt
│       ├── TaskSchedulerService.kt
│       ├── TaskExecutorReceiver.kt
│       └── BootReceiver.kt
├── res/
│   ├── layout/                      # 界面布局
│   ├── values/                      # 资源文件
│   ├── drawable/                    # 图标
│   └── xml/                         # 配置文件
└── AndroidManifest.xml              # 应用清单
```

## 权限说明

应用需要以下权限：

| 权限 | 用途 |
|------|------|
| `INTERNET` | 网络通信 |
| `READ_EXTERNAL_STORAGE` | 读取图片 |
| `WRITE_EXTERNAL_STORAGE` | 写入文件 |
| `SCHEDULE_EXACT_ALARM` | 精确定时 |
| `POST_NOTIFICATIONS` | 发送通知 |
| `RECEIVE_BOOT_COMPLETED` | 开机启动 |
| `BIND_ACCESSIBILITY_SERVICE` | 无障碍服务 |

## 常见问题

### Q: 应用无法发送消息？
A: 请确保：
1. 已启用无障碍服务
2. 微信应用已安装
3. 群名称输入正确
4. 网络连接正常

### Q: 定时任务没有执行？
A: 请检查：
1. 设备是否处于低电量模式
2. 应用是否被系统休眠
3. 定时时间设置是否正确

### Q: 如何卸载应用？
A: 在系统设置中找到"微信定时发送"，点击卸载即可。

## 开发信息

- **语言**：Kotlin
- **最低 API**：24 (Android 7.0)
- **目标 API**：34 (Android 14)
- **构建工具**：Gradle 8.1.0

## 许可证

MIT License

## 支持

如有问题或建议，欢迎反馈。

---

**注意**：本应用仅供学习和个人使用，请遵守微信服务条款。
