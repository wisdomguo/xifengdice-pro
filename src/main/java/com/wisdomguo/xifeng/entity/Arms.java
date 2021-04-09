package com.wisdom.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdom.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Arms
 * 武器实体类
 * @author wisdom-guo
 * @since 2021/3/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("arms")
public class Arms extends BaseEntity {

    /**
     * 武器名称
     */
    private String armsName;

    /**
     * 伤害骰个数
     */
    private Integer injuryDiceCount;

    /**
     * 伤害骰大小
     */
    private Integer injuryDiceSize;

    /**
     * 武器类型
     * 轻型
     * 正常
     * 双手
     */
    private Integer hold;

    /**
     * 体型
     * 超巨型(盾牌限定
     * 巨型(盾牌限定
     * 超大
     * 大
     * 中
     * 小
     * 超小
     */
    private Integer bodyType;

    /**
     * 武器类型
     * 远近武器/盾牌
     */
    private Integer armsType;

    /**
     * 攻击范围
     */
    private Integer attackDistance;

    /**
     * 力量加值上线
     * -1为吃全部加值
     */
    private Integer strength;

    /**
     * 精制品加成+1鉴定(远程弹药精制品不叠加
     */
    private Integer refinedProducts;

    /**
     * 加值附魔
     */
    private Integer bonusEnchantment;

    /**
     * 伤害类型 敲击 穿刺 挥砍
     */
    private Integer injuryType;

    /**
     * 暴击率
     */
    private Integer critChance;

    /**
     * 暴击伤害
     */
    private Integer criticalDamage;

    /**
     * 防御加值 AC
     */
    private Integer armorClassUp;
}
