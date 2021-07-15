package com.wisdomguo.xifeng.modules.game.farm.seedspecies.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SeedSpecies
 * 种子类型
 * @author wisdom-guo
 * @since 2021/7/9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("seed_species")
public class SeedSpecies {

    @TableId
    private Integer id;
    /**
     * 名字
     */
    private String name;
    /**
     * 购买币种
     */
    private int type;
    /**
     * 价格
     */
    private int price;
    /**
     * 最低售价
     */
    private int minSell;
    /**
     * 最高售价
     */
    private int maxSell;
    /**
     * 收获次数
     */
    private int times;
    /**
     * 种植时间
     */
    private int duration;
    /**
     * 单位
     */
    private String unit;
}
