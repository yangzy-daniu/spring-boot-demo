package com.example.demo.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class CategorySalesDTO {
    private String category;
    private Double value; // 可以是销量或金额
    private String color; // 前端固定颜色映射

    // 默认构造函数（需要用于反序列化）
    public CategorySalesDTO() {
    }

    // 带参数的构造函数
    public CategorySalesDTO(String category, Double value) {
        this.category = category;
        this.value = value;
        // 可以根据category设置默认颜色
        this.color = getDefaultColor(category);
    }

    // 完整的颜色映射，包含更多品类
    private static final Map<String, String> COLOR_MAP = new HashMap<>();

    static {
        // 扩展颜色映射，确保每个品类都有独特颜色
        COLOR_MAP.put("电子产品", "#5470c6");
        COLOR_MAP.put("家用电器", "#91cc75");
        COLOR_MAP.put("服装鞋帽", "#fac858");
        COLOR_MAP.put("图书文具", "#ee6666");
        COLOR_MAP.put("数码配件", "#73c0de");
        COLOR_MAP.put("运动户外", "#3ba272");
        COLOR_MAP.put("美妆个护", "#fc8452");
        COLOR_MAP.put("食品饮料", "#9a60b4");
        COLOR_MAP.put("家居用品", "#ea7ccc");
        COLOR_MAP.put("办公设备", "#1e90ff");
        COLOR_MAP.put("汽车用品", "#ff6347");
        COLOR_MAP.put("母婴用品", "#32cd32");
    }

    private String getDefaultColor(String category) {
        // 如果映射中有该品类，返回对应颜色
        if (COLOR_MAP.containsKey(category)) {
            return COLOR_MAP.get(category);
        }

        // 对于不在映射中的品类，使用稳定的哈希算法生成颜色
        return generateStableColor(category);
    }

    private String generateStableColor(String category) {
        // 使用品类名称的哈希值生成稳定的颜色
        int hash = Math.abs(category.hashCode());

        // 预定义一组美观的颜色
        String[] colorPalette = {
                "#5470c6", "#91cc75", "#fac858", "#ee6666",
                "#73c0de", "#3ba272", "#fc8452", "#9a60b4",
                "#ea7ccc", "#1e90ff", "#ff6347", "#32cd32",
                "#9370db", "#20b2aa", "#ff4500", "#da70d6",
                "#6495ed", "#ff69b4", "#ba55d3", "#4682b4"
        };

        // 根据哈希值选择颜色，确保相同品类总是得到相同颜色
        int index = hash % colorPalette.length;
        return colorPalette[index];
    }
}