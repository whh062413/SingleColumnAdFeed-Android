# SingleColumnAdFeed-Android

> 基于 **Android + Jetpack Compose** 的单列广告信息流应用，支持 AI 智能广告分析。

<p align="center">
  <img src="https://img.shields.io/badge/Android-7.0%2B-34A853?logo=android" alt="Android">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-Material%203-4285F4?logo=jetpackcompose" alt="Compose">
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue" alt="Target SDK">
</p>

---

## ✨ 功能

| 模块 | 说明 |
|------|------|
| 📱 信息流主页 | 单列卡片瀑布流，支持下拉刷新 + 无限滚动分页 |
| 🎴 三种卡片 | 大图模式 / 小图模式 / 视频模式（Media3 ExoPlayer） |
| 🏷️ 分类切换 | 推荐 · 电商 · 本地，三个 Tab 即时切换 |
| 🤖 AI 广告分析 | 云端生成广告摘要 + 智能标签，失败自动降级本地规则 |
| 🔍 AI 搜索 | 云端拆词优先，本地分词降级，匹配标题 / 描述 / 标签 |
| ❤️ 互动反馈 | 点赞 / 收藏，SQLite 持久化，列表与详情页实时同步 |
| 📊 埋点追踪 | 曝光判定（可见 ≥50% 持续 ≥1s）+ 点击事件 |
| 🎨 Material 3 | Google Blue 主题色，骨架屏 shimmer 加载动画 |

---

## 🚀 快速开始

### 1. 配置 AI API Key

> ⚠️ AiConfig.java 已加入 .gitignore，clone 后需**手动创建**。

在 pp/src/main/java/com/wuyiming/singlecolumnadfeed_android/config/ 下新建 AiConfig.java：

`java
package com.wuyiming.singlecolumnadfeed_android.config;

public final class AiConfig {
    private AiConfig() {}

    /** 填写你的 API Key，留空则禁用 AI 功能 */
    public static String apiKey = "";

    /** OpenAI 兼容的 API 地址（支持 DeepSeek / OpenAI / 通义千问 等） */
    public static String apiUrl = "https://api.deepseek.com/v1/chat/completions";

    /** 模型名称 */
    public static String model = "deepseek-v4-flash";

    public static boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
`

### 2. 构建 & 安装

`bash
./gradlew assembleDebug    # 构建 Debug APK
./gradlew installDebug     # 安装到已连接设备
`

---

## 🏗️ 架构

`
┌──────────────────────────────────────┐
│  UI Layer (Kotlin / Compose)         │
│  FeedScreen → AdCardFactory          │
│  DetailScreen                        │
└──────────────┬───────────────────────┘
               │ StateFlow
┌──────────────▼───────────────────────┐
│  ViewModel (Kotlin)                  │
│  FeedViewModel — 全局单例            │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│  Repository (Java)                   │
│  DefaultFeedRepository               │
│     ↳ MockFeedDataSource (Mock)      │
│     ↳ FeedInteractionStore (SQLite)  │
│     ↳ AiInsightGenerator (AI)        │
└──────────────────────────────────────┘
`

**MVVM + Repository** 模式，数据层全部使用 Java，UI 层保留 Kotlin/Compose。

---

## 📁 项目结构

`
app/src/main/java/com/wuyiming/singlecolumnadfeed_android/
│
├── MainActivity.java                  # 入口 Activity
│
├── ai/                                # AI 服务
│   ├── AiInsightGenerator.java        # 广告摘要 + 标签生成
│   └── AiSearchService.java           # 搜索关键词提取
│
├── config/
│   └── AiConfig.java                  # API 密钥配置 (gitignored)
│
├── data/
│   ├── local/
│   │   └── FeedInteractionStore.java  # SQLite 互动数据持久化
│   ├── mock/
│   │   └── MockFeedDataSource.java    # 100 条中文 Mock 数据
│   ├── model/
│   │   ├── FeedItem.java              # 核心数据模型
│   │   ├── FeedItemType.java          # 卡片类型枚举
│   │   ├── FeedCategory.java          # 分类枚举
│   │   └── AdInsight.java             # AI 分析结果
│   ├── repository/
│   │   ├── FeedRepository.kt          # 数据仓库接口 (suspend)
│   │   └── DefaultFeedRepository.java # 仓库实现
│   └── video/
│       └── VideoCacheManager.java     # 视频本地缓存
│
├── network/
│   ├── AiApiService.java              # OpenAI 兼容 API 调用
│   └── OkHttpProvider.java            # OkHttp 单例提供
│
├── tracking/
│   ├── AdTracker.java                 # 曝光 & 点击埋点
│   └── TrackingModels.java            # 埋点事件模型
│
├── ui/
│   ├── components/
│   │   ├── AdCardFactory.kt           # 广告卡片 Compose 组件
│   │   ├── ScreenStateView.kt         # 骨架屏 / 空状态 / 错误状态
│   │   ├── VideoPlayerPool.java       # 视频播放器池接口
│   │   ├── SimpleVideoPlayerPool.java # 播放器池实现
│   │   └── FeedVideoPlayerPool.java   # 播放器池单例
│   ├── detail/
│   │   ├── DetailActivity.kt          # 详情页 Activity
│   │   └── DetailScreen.kt            # 详情页 Compose
│   ├── feed/
│   │   └── FeedScreen.kt              # 信息流主页
│   └── theme/
│       └── Theme.kt                   # Material 3 主题 + Compose 桥接
│
└── viewmodel/
    └── FeedViewModel.kt               # 全局状态管理
`

---

## 🛠️ 技术栈

| 类别 | 选型 |
|------|------|
| 语言 | **Java** (数据层) + **Kotlin** (UI 层) |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Repository |
| 状态管理 | StateFlow + collectAsState |
| 异步 | Kotlin Coroutines |
| 视频 | Media3 ExoPlayer |
| 图片 | Coil (Compose) |
| 网络 | OkHttp 4.x |
| JSON | Gson |
| 本地存储 | SQLite (手写 DAO，无 Room) |
| AI | OpenAI 兼容 API |
| 最低 SDK | API 24 (Android 7.0) |
| 编译 SDK | API 36 |
| Java | 17 |

---

## 📝 环境要求

- **Android Studio** Hedgehog (2023.1.1) 或更高
- **JDK** 17
- **Android SDK** API 36
- **Gradle** 8.x

---

## 📄 许可

[MIT License](LICENSE)

---

*最后更新：2026-06-14*