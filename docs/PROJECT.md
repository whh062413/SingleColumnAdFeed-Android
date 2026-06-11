# SingleColumnAdFeed-Android 项目详解

## 项目概述

SingleColumnAdFeed-Android 是一款基于 **Android + Jetpack Compose** 的**单列广告信息流**应用。
以卡片瀑布流形式展示广告内容，支持分类切换、AI 智能分析、搜索筛选、点赞收藏等交互功能。

---

## 技术栈

| 类别 | 技术选型 |
| --- | --- |
| 语言 | Java（数据层/逻辑层） + Kotlin（UI 层） |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构模式 | MVVM + Repository |
| 网络 | OkHttp 4.x |
| JSON | Gson |
| 图片加载 | Coil (Compose) |
| 本地存储 | SQLite (Room-free, 手写 DAO) |
| AI 集成 | OpenAI 兼容 API (DeepSeek 等) |
| 最低 SDK | API 24 (Android 7.0) |
| 编译 SDK | API 36 |
| 目标 SDK | API 36 |
| Java 版本 | 17 |

---

## 工程结构

```
SingleColumnAdFeed-Android/
├── app/
│   ├── build.gradle.kts          # 应用构建配置
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/wuyiming/singlecolumnadfeed_android/
│       │   ├── MainActivity.java              # 入口 Activity
│       │   ├── MainContent.kt                 # Compose 桥接
│       │   ├── ai/                            # AI 服务层
│       │   │   ├── AiInsightGenerator.java    # AI 广告摘要生成
│       │   │   └── AiSearchService.java       # AI 搜索关键词提取
│       │   ├── config/
│       │   │   └── AiConfig.java              # AI API 配置
│       │   ├── data/
│       │   │   ├── local/                     # 本地持久化
│       │   │   │   ├── InteractionStore.java
│       │   │   │   └── db/
│       │   │   │       ├── InteractionDatabase.java
│       │   │   │       └── InteractionDao.java
│       │   │   ├── mock/                      # Mock 数据
│       │   │   │   ├── MockDataSource.java
│       │   │   │   └── MockFeedDataSource.java
│       │   │   ├── model/                     # 数据模型
│       │   │   │   ├── FeedItem.java
│       │   │   │   ├── FeedItemType.java
│       │   │   │   ├── FeedCategory.java
│       │   │   │   └── AdInsight.java
│       │   │   └── repository/                # 数据仓库
│       │   │       ├── FeedRepository.java
│       │   │       └── DefaultFeedRepository.java
│       │   ├── network/                       # 网络层
│       │   │   ├── AiApiService.java
│       │   │   └── OkHttpProvider.java
│       │   ├── tracking/                      # 埋点追踪
│       │   │   ├── AdTracker.java
│       │   │   └── TrackingModels.java
│       │   ├── ui/                            # UI 层
│       │   │   ├── components/
│       │   │   │   ├── AdCardFactory.kt       # 广告卡片组件
│       │   │   │   └── SkeletonLoading.kt     # 骨架屏
│       │   │   ├── detail/
│       │   │   │   ├── DetailActivity.kt      # 详情页 Activity
│       │   │   │   └── DetailScreen.kt        # 详情页 Compose
│       │   │   ├── feed/
│       │   │   │   └── FeedScreen.kt          # 信息流主页
│       │   │   └── theme/
│       │   │       └── Theme.kt               # Material 3 主题
│       │   └── viewmodel/                     # ViewModel 层
│       │       ├── FeedViewModel.java
│       │       └── FeedUiState.java
│       └── res/                               # 资源文件
│           ├── drawable-nodpi/                # 10 张广告封面图
│           ├── layout/activity_main.xml
│           └── values/                        # 颜色、字符串、主题
├── build.gradle.kts           # 根构建配置
├── settings.gradle.kts
└── gradle/libs.versions.toml  # 版本目录
```

---

## 架构设计

### MVVM + Repository 架构

```
┌─────────────────────────────────────────────────┐
│  UI Layer (Kotlin / Compose)                    │
│  FeedScreen → AdCardFactory / DetailScreen      │
└────────────────────┬────────────────────────────┘
                     │ observe LiveData
┌────────────────────▼────────────────────────────┐
│  ViewModel Layer (Java)                         │
│  FeedViewModel (Singleton AndroidViewModel)      │
│  - 分页加载 / 下拉刷新 / 分类切换 / 搜索         │
│  - AI 摘要异步生成                               │
└──────┬────────────────────────────┬─────────────┘
       │                            │
┌──────▼──────────┐    ┌───────────▼──────────┐
│  Repository      │    │  AI Service Layer     │
│  DefaultFeedRepo │    │  AiInsightGenerator   │
│  (Mock Data)     │    │  AiSearchService      │
└──────┬──────────┘    └───────────┬──────────┘
       │                           │
┌──────▼──────────┐    ┌───────────▼──────────┐
│  Local Storage   │    │  Network Layer        │
│  SQLite          │    │  OkHttp + Gson        │
│  Interaction DB  │    │  OpenAI-compatible API│
└─────────────────┘    └──────────────────────┘
```

### 线程模型

FeedViewModel 使用三个独立的 `ExecutorService`：

| 线程池 | 用途 | 说明 |
| --- | --- | --- |
| `dataExecutor` | 数据加载 + AI 生成 | 单线程，保证顺序 |
| `searchExecutor` | 搜索处理 | 独立线程，不被数据加载阻塞 |
| `refreshExecutor` | 下拉刷新 | 独立线程，不被 AI 生成阻塞 |

---

## 功能模块详解

### 1. 数据模型 (`data/model/`)

#### FeedItem — 广告内容实体

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | String | 唯一标识 (mock_1 ~ mock_100) |
| `title` | String | 广告标题 |
| `description` | String | 广告描述 |
| `coverUrl` | String | 封面图 (android.resource:// URI) |
| `videoUrl` | String | 视频链接 (预留) |
| `feedItemType` | FeedItemType | 卡片类型 |
| `category` | FeedCategory | 分类 |
| `likeCount` | int | 点赞数 |
| `commentCount` | int | 评论数 |
| `isLiked` | boolean | 是否已点赞 |
| `isCollected` | boolean | 是否已收藏 |
| `insight` | AdInsight | AI 分析结果 |
| `timestamp` | long | 时间戳 |

使用 Builder 模式构建，实现 `Serializable` 以支持 Intent 传递。

#### FeedItemType — 卡片展示类型

- `BIG_IMAGE` — 大图模式 (220dp 封面)
- `SMALL_IMAGE` — 小图模式 (100dp 方形封面)
- `VIDEO` — 视频模式 (暂与大图一致显示)

#### FeedCategory — 内容分类

- `RECOMMEND` — 推荐
- `ECOMMERCE` — 电商
- `LOCAL` — 本地

#### AdInsight — AI 分析结果

- `summary` — AI 生成的广告摘要（≤30字）
- `tags` — AI 生成的标签列表（3-5个）

---

### 2. Mock 数据层 (`data/mock/`)

`MockFeedDataSource` 生成 100 条模拟广告数据：

- **标题池**：10 组商品前缀 + 后缀，覆盖美妆、教育、日用品、家居、饮料、饰品、手表、水果等品类
- **封面图**：10 张本地 drawable 图片 (`ad_cover_1.jpg` ~ `ad_cover_10.jpeg`)
- **分类分布**：推荐 / 电商 / 本地按 `i % 3` 轮换
- **卡片类型**：大图 / 小图按 `i % 3` 轮换
- **预置标签**：每条数据预置 5 个中文标签，保证离线搜索可用
- 每次调用 `generateFeedItems()` 时打乱顺序

---

### 3. 数据仓库 (`data/repository/`)

#### FeedRepository (接口)

定义标准数据访问契约：分页加载、分类筛选、点赞/收藏切换、ID 查询。

#### DefaultFeedRepository (实现)

- 单例模式，持有 100 条 mock 数据
- 分页加载：每页 20 条，按 `(page-1)*pageSize` 到 `end` 切片
- 分类筛选：通过 Stream filter 按 `FeedCategory` 过滤
- `reloadData()` 重新生成数据并同步本地点赞状态
- 点赞/收藏操作委托给 `InteractionStore`

---

### 4. 本地持久化 (`data/local/`)

#### InteractionDatabase — SQLite 数据库

```sql
CREATE TABLE interactions (
    feed_id       TEXT PRIMARY KEY,
    is_liked      INTEGER NOT NULL DEFAULT 0,
    is_collected  INTEGER NOT NULL DEFAULT 0,
    updated_at    INTEGER NOT NULL
)
```

#### InteractionDao — 数据访问

- `hasInteraction(feedId)` — 判断是否有交互记录
- `isLiked(feedId)` / `setLiked(feedId, liked)` — 点赞读写
- `isCollected(feedId)` / `setCollected(feedId, collected)` — 收藏读写
- `getAllLikedIds()` / `getAllCollectedIds()` — 批量查询

#### InteractionStore — 门面类

封装 InteractionDao，对上层提供简洁 API。

---

### 5. AI 服务层 (`ai/`)

#### AiInsightGenerator — AI 广告分析

**工作流程**：
1. 尝试云端 AI 生成（调用 OpenAI 兼容 API）
2. 失败时降级到本地规则生成

**云端模式**：
- System Prompt：要求 AI 以广告分析专家身份，生成 ≤30 字中文摘要 + 3-5 个标签
- 返回 JSON：`{"summary": "...", "tags": ["..."]}`

**本地降级**：
- 摘要：截取描述前 47 字符 + "..."
- 标签：关键词匹配（电商/优惠/促销/本地/科技/美食/美容/教育/旅游 等 20+ 词库）

#### AiSearchService — AI 搜索关键词提取

**工作流程**：
1. 短查询（≤2字）直接使用原始词
2. 长查询调用 AI 拆解为 3-8 个关键词
3. 失败时降级到标点分割

**云端模式**：
- System Prompt：中文搜索关键词提取器，返回 JSON 数组
- 返回：`["关键词1", "关键词2", ...]`

---

### 6. 网络层 (`network/`)

#### AiApiService

OpenAI 兼容 Chat Completions API 客户端：
- `callRawApi(systemPrompt, userMessage, apiKey)` — 通用调用，返回 AI 文本响应
- `callCloudApi(title, description, apiKey)` — 广告分析专用，返回 AdInsight
- 参数：model / temperature=0.3 / max_tokens=500

#### OkHttpProvider

单例 OkHttpClient，超时配置：连接 10s、读写 30s。

---

### 7. ViewModel (`viewmodel/`)

#### FeedViewModel — 核心业务逻辑

全局单例 `AndroidViewModel`，管理整个应用状态。

**分页加载**：`PAGE_SIZE = 20`，支持 `loadMore()` 追加加载。

**下拉刷新**：独立 `refreshExecutor`，确保不被 AI 生成阻塞。最小刷新动画 600ms。

**分类切换**：`switchCategory(category)` — 重新加载对应分类数据。

**搜索功能**：
- 优先使用 AiSearchService 云端拆词
- 降级到本地标点分割
- 在 title + description + tags 中进行模糊匹配
- `searchGeneration` 原子计数器防止并发结果覆盖

**AI 摘要生成**：
- 数据加载后自动触发
- 跳过已有 summary 的项
- 每次生成后更新 UI

**点赞/收藏**：通过 Repository 委托到 InteractionStore 持久化。

#### FeedUiState — UI 状态

| 字段 | 说明 |
| --- | --- |
| `loading` | 首次加载中 |
| `refreshing` | 下拉刷新中 |
| `loadingMore` | 加载更多中 |
| `items` | 当前展示列表 |
| `currentCategory` | 当前分类 |
| `hasMore` | 是否还有更多数据 |
| `isSearching` | 搜索进行中 |
| `isSearchMode` | 搜索模式下（不触发加载更多） |

---

### 8. UI 层 (`ui/`)

#### FeedScreen — 信息流主页

- **TopAppBar**：标题 "广告信息流"
- **CategoryTabs**：推荐 / 电商 / 本地 三个 Tab
- **SearchBar**：搜索输入框，支持键盘搜索和清除
- **PullToRefreshBox**：Material 3 下拉刷新
- **LazyColumn**：
  - 搜索模式下显示结果数量
  - 首次加载显示 5 个骨架屏
  - 数据项通过 AdCardFactory 渲染
  - 加载更多时显示底部 loading
  - 无更多数据时显示 "没有更多了"
- **无限滚动**：`snapshotFlow` 监听滚动位置，距底部 3 项时自动加载更多

#### AdCardFactory — 广告卡片

- 大图模式：220dp 封面
- 小图模式：100dp 方形封面
- 显示 AI 标签（最多 3 个 SuggestionChip）
- 显示标题 + AI 摘要
- 交互栏：点赞按钮 + 评论按钮

#### DetailScreen — 详情页

- 顶部返回箭头
- 封面图（根据类型不同高度）
- 标题 + 描述
- AI 分析卡片（primaryContainer 背景，summary + tags）
- 点赞按钮（带动画状态）

#### SkeletonLoading — 骨架屏

Shimmer 动画的占位卡片，首次加载时展示。

#### Theme — 主题

Material 3 浅色主题：
- primary: `#1A73E8` (Google Blue)
- primaryContainer: `#D2E3FC`
- surface: `#F8F9FA`
- error: `#EA4335` (Google Red)

---

### 9. 埋点追踪 (`tracking/`)

#### AdTracker — 曝光 & 点击追踪

- **曝光判定**：可见比例 ≥50% 且持续 ≥1 秒
- **去重**：同一 item 只计一次有效曝光
- **点击**：记录 feedId 和时间戳

#### TrackingModels

- `ExposureEvent(feedId, timestamp, visibleRatio)`
- `ClickEvent(feedId, timestamp)`

---

### 10. 配置 (`config/`)

#### AiConfig.java

集中管理 AI API 配置：
- `apiKey`：API 密钥（已预填 DeepSeek Key）
- `apiUrl`：`https://api.deepseek.com/v1/chat/completions`
- `model`：`deepseek-v4-flash`
- `isConfigured()`：判断 AI 是否可用

---

## 当前进度总结

### ✅ 已完成

- 完整的 MVVM + Repository 架构
- 单列信息流主页（Compose LazyColumn + 无限滚动）
- 三种卡片类型（大图/小图/视频）
- 三个分类 Tab 切换（推荐/电商/本地）
- 下拉刷新 + 骨架屏加载
- 搜索功能（AI 云端拆词 + 本地降级）
- AI 广告摘要 & 标签生成（云端 + 本地降级）
- 详情页（封面 + 内容 + AI 分析卡片）
- 点赞/收藏（SQLite 持久化）
- 曝光 & 点击埋点追踪
- Material 3 主题
- 100 条中文 Mock 数据 + 10 张封面图
- 多线程 Executor 隔离（数据/AI/刷新互不阻塞）

### 🚧 待开发

- VIDEO 类型的视频播放功能
- 评论列表与评论交互
- 真实后端 API 对接（替代 Mock 数据）
- 用户登录/个人中心
- 深色模式适配
- 单元测试 / UI 测试
- 性能优化（图片缓存策略、列表回收）

---

## 运行指南

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Gradle 8.x
- Android SDK API 36

### 构建 & 运行

```bash
# 克隆项目
git clone <repo-url>
cd SingleColumnAdFeed-Android

# 同步依赖并构建
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

### AI 功能配置

编辑 `app/src/main/java/.../config/AiConfig.java`：

```java
public static String apiKey = "your-api-key";    // 填写 API Key
public static String apiUrl = "https://api.deepseek.com/v1/chat/completions";
public static String model = "deepseek-v4-flash";
```

留空 `apiKey` 即可禁用 AI 功能，应用将使用本地规则降级方案。

---

*最后更新：2026-06-11*
