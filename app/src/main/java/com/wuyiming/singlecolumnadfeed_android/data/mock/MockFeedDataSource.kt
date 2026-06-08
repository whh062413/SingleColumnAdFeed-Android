package com.wuyiming.singlecolumnadfeed_android.data.mock

import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType

object MockFeedDataSource {

    private const val PACKAGE_NAME = "com.wuyiming.singlecolumnadfeed_android"

    private val coverImages = listOf(
        "ad_cover_1", "ad_cover_2", "ad_cover_3", "ad_cover_4", "ad_cover_5",
        "ad_cover_6", "ad_cover_7", "ad_cover_8", "ad_cover_9", "ad_cover_10"
    )

    private val tagPool = listOf(
        listOf("美妆", "化妆品", "高颜值", "爆款", "学生党"),
        listOf("教育", "教材", "畅销书", "学习必备", "高分"),
        listOf("日用品", "清洁", "杀菌", "家庭装", "实惠"),
        listOf("家居", "厨房好物", "高颜值", "实用", "爆款"),
        listOf("饮料", "夏日必备", "清爽", "聚会", "特价"),
        listOf("饰品", "手串", "文玩", "送礼", "精致"),
        listOf("手表", "时尚配饰", "品质", "商务", "礼物"),
        listOf("水果", "樱桃", "新鲜", "产地直发", "当季"),
        listOf("水果", "橙子", "鲜甜", "多汁", "包邮"),
        listOf("日用品", "水杯", "便携", "高颜值", "学生党")
    )

    private val titleTemplates = listOf(
        Triple("大牌平替化妆盒", " | 精致女孩必备收纳神器", "大图"),
        Triple("2025新版教材全解", " | 学霸都在用的提分宝典", "小图"),
        Triple("抑菌洗手液家庭装", " | 温和不伤手 限时买二送一", "大图"),
        Triple("高颜值恒温烧水壶", " | 3分钟沸腾 颜值与实力并存", "小图"),
        Triple("雪碧冰爽整箱囤", " | 夏日续命水 聚会必备", "大图"),
        Triple("天然玉石手串", " | 招财转运 自戴送人都体面", "小图"),
        Triple("轻奢石英手表", " | 简约百搭 通勤约会一支搞定", "大图"),
        Triple("现摘大樱桃顺丰包邮", " | 颗颗爆汁 果园直发新鲜到家", "小图"),
        Triple("赣南脐橙当季鲜摘", " | 甜到心坎 10斤家庭装", "大图"),
        Triple("网红高颜值随手杯", " | 耐摔防漏 上课通勤必备", "小图")
    )

    private val descriptions = listOf(
        "多层分区大容量｜化妆品收纳一步到位｜小红书万人种草同款",
        "名师编写考点全覆盖｜同步课堂进度｜随书赠真题卷",
        "99.9%有效抑菌｜pH中性不伤手｜500ml大容量家庭装",
        "304不锈钢内胆｜智能恒温保温｜颜值在线百搭各种厨房",
        "原装正品整箱24罐｜冰镇更过瘾｜烧烤火锅绝配",
        "天然玉石手工打磨｜温润细腻上手显白｜附精美礼盒",
        "进口机芯走时精准｜30米生活防水｜商务休闲两不误",
        "当天采摘当天发货｜颗颗25mm以上大果｜坏果包赔",
        "赣南核心产区直供｜糖度实测15°+｜皮薄肉厚汁水足",
        "Tritan材质安心无异味｜500ml黄金容量｜多色可选"
    )

    private val aiSummaries = listOf(
        "AI 摘要：该化妆盒近期社媒讨论量飙升，平价好用的口碑使其复购率持续走高，适合学生党和职场新人入手。",
        "AI 摘要：本教材系列好评率超98%，覆盖核心考点精准，历年使用者成绩提升明显，是公认的提分利器。",
        "AI 摘要：洗手液品类近期搜索量上涨显著，家庭装性价比突出，用户评价集中在温和不刺激和杀菌效果两方面。",
        "AI 摘要：烧水壶品类消费升级趋势明显，颜值和功能并重的产品更受年轻消费者青睐，本品复购率高于同类20%。",
        "AI 摘要：夏日饮品进入销售旺季，雪碧作为经典品牌搜索量和转化率均处高位，整箱囤货成主流购买方式。",
        "AI 摘要：文玩手串热度持续攀升，年轻消费群体占比逐年提高，兼具审美和寓意的产品更受欢迎。",
        "AI 摘要：轻奢手表市场增长稳健，简约设计风格占据主流，消费者更关注机芯品质和佩戴舒适度。",
        "AI 摘要：当季樱桃搜索热度环比增长显著，产地直发模式成为用户首选，新鲜度和坏果率是核心决策因素。",
        "AI 摘要：脐橙品类复购率高居水果榜首，家庭装最受欢迎，用户最关注甜度口感和物流速度。",
        null
    )

    fun generateFeedItems(count: Int): List<FeedItem> {
        return List(count) { index ->
            val type = when (index % 3) {
                0 -> FeedItemType.BIG_IMAGE
                1 -> FeedItemType.SMALL_IMAGE
                else -> FeedItemType.BIG_IMAGE
            }
            val category = when (index % 3) {
                0 -> FeedCategory.RECOMMEND
                1 -> FeedCategory.ECOMMERCE
                else -> FeedCategory.LOCAL
            }

            val coverIndex = index % coverImages.size
            val titleTpl = titleTemplates[index % titleTemplates.size]
            val categoryLabel = when (category) {
                FeedCategory.RECOMMEND -> "精选"
                FeedCategory.ECOMMERCE -> "电商"
                FeedCategory.LOCAL -> "本地"
            }
            val displayNum = index + 1
            val title = "[$categoryLabel] ${titleTpl.first} #$displayNum ${titleTpl.second}"

            val coverUrl = "android.resource://$PACKAGE_NAME/drawable/${coverImages[coverIndex]}"

            FeedItem.Builder()
                .id("mock_${displayNum}")
                .title(title)
                .description(descriptions[index % descriptions.size])
                .coverUrl(coverUrl)
                .feedItemType(type)
                .category(category)
                .likeCount(128 + index * 17)
                .commentCount(12 + index * 3)
                .isLiked(index % 5 == 0)
                .isCollected(index % 7 == 0)
                .insight(
                    aiSummaries[index % aiSummaries.size]?.let { summary ->
                        AdInsight(summary, tagPool[index % tagPool.size])
                    }
                )
                .build()
        }
    }
}
