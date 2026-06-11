package com.wuyiming.singlecolumnadfeed_android.data.mock;

import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generates mock feed data. Items are shuffled for variety on each call.
 * Default tags are pre-assigned so search works immediately, before async AI refinement.
 */
public class MockFeedDataSource {

    private static final String PACKAGE_NAME = "com.wuyiming.singlecolumnadfeed_android";

    private static final List<String> COVER_IMAGES = Arrays.asList(
            "ad_cover_1", "ad_cover_2", "ad_cover_3", "ad_cover_4", "ad_cover_5",
            "ad_cover_6", "ad_cover_7", "ad_cover_8", "ad_cover_9", "ad_cover_10"
    );

    private static final List<List<String>> TAG_POOL = Arrays.asList(
            Arrays.asList("美妆", "化妆盒", "高颜值", "爆款", "学生党"),
            Arrays.asList("教育", "教材", "畅销书", "学习必备", "高分"),
            Arrays.asList("日用品", "清洁", "杀菌", "家庭装", "实惠"),
            Arrays.asList("家居", "厨房好物", "高颜值", "实用", "爆款"),
            Arrays.asList("饮料", "夏日必备", "清爽", "聚会", "特价"),
            Arrays.asList("饰品", "手串", "文玩", "送礼", "精致"),
            Arrays.asList("手表", "时尚配饰", "品质", "商务", "礼物"),
            Arrays.asList("水果", "樱桃", "新鲜", "产地直发", "当季"),
            Arrays.asList("水果", "橙子", "鲜甜", "多汁", "包邮"),
            Arrays.asList("日用品", "水杯", "便携", "高颜值", "学生党")
    );

    private static final List<String> TITLE_PREFIXES = Arrays.asList(
            "大牌平替化妆盒", "2025新版教材全解", "抑菌洗手液家庭装",
            "高颜值恒温烧水壶", "雪碧冰爽整箱囤", "天然玉石手串",
            "轻奢石英手表", "现摘大樱桃顺丰包邮", "赣南脐橙当季鲜摘",
            "网红高颜值随手杯"
    );

    private static final List<String> TITLE_SUFFIXES = Arrays.asList(
            " | 精致女孩必备收纳神器", " | 学霸都在用的提分宝典",
            " | 温和不伤手 限时买二送一", " | 3分钟沸腾 颜值与实力并存",
            " | 夏日续命水 聚会必备", " | 招财转运 自戴送人都体面",
            " | 简约百搭 通勤约会一支搞定", " | 颗颗爆汁 果园直发新鲜到家",
            " | 甜到心坎 10斤家庭装", " | 耐摔防漏 上课通勤必备"
    );

    private static final List<String> DESCRIPTIONS = Arrays.asList(
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
    );

    public static List<FeedItem> generateFeedItems(int count) {
        java.util.List<FeedItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            FeedItemType type;
            if (i % 3 == 0) {
                type = FeedItemType.BIG_IMAGE;
            } else if (i % 3 == 1) {
                type = FeedItemType.SMALL_IMAGE;
            } else {
                type = FeedItemType.BIG_IMAGE;
            }

            FeedCategory category;
            if (i % 3 == 0) {
                category = FeedCategory.RECOMMEND;
            } else if (i % 3 == 1) {
                category = FeedCategory.ECOMMERCE;
            } else {
                category = FeedCategory.LOCAL;
            }

            int idx = i % TITLE_PREFIXES.size();
            String prefix = TITLE_PREFIXES.get(idx);
            String suffix = TITLE_SUFFIXES.get(idx);
            String categoryLabel;
            if (category == FeedCategory.RECOMMEND) {
                categoryLabel = "精选";
            } else if (category == FeedCategory.ECOMMERCE) {
                categoryLabel = "电商";
            } else {
                categoryLabel = "本地";
            }
            int displayNum = i + 1;
            String title = "[" + categoryLabel + "] " + prefix + " #" + displayNum + suffix;

            String coverUrl = "android.resource://" + PACKAGE_NAME + "/drawable/" + COVER_IMAGES.get(i % COVER_IMAGES.size());

            // Pre-assign default tags so search works immediately (AI refines later)
            List<String> defaultTags = TAG_POOL.get(idx);
            AdInsight insight = new AdInsight(null, new java.util.ArrayList<>(defaultTags));

            FeedItem item = new FeedItem.Builder()
                    .id("mock_" + displayNum)
                    .title(title)
                    .description(DESCRIPTIONS.get(idx))
                    .coverUrl(coverUrl)
                    .feedItemType(type)
                    .category(category)
                    .likeCount(128 + (i * 17 % 1000))
                    .commentCount(12 + (i * 3 % 50))
                    .isLiked(i % 5 == 0)
                    .isCollected(i % 7 == 0)
                    .insight(insight)
                    .build();

            items.add(item);
        }

        Collections.shuffle(items);
        return items;
    }
}