package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Equipment
 * 装备实体类
 * @author wisdom-guo
 * @since 2021/3/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("equipment")
public class Equipment extends BaseEntity {

    /**
     * 装备类型
     * 头部(头巾,帽子
     * 面部(眼镜,面具
     * 喉部(护符
     * 肩部(披风
     * 身体(盔甲,长袍
     * 躯干部(法衣,衬衫
     * 腰部(腰带
     * 臂部(护腕,手镯
     * 手部(手套
     * 戒指2个
     * 足部(鞋子
     * 武器
     */
    private Integer equipmentType;

    /**
     * 力量增加
     */
    private Integer powerUp;

    /**
     * 敏捷增加
     */
    private Integer agileUp;

    /**
     * 体质增加
     */
    private Integer constitutionUp;

    /**
     * 感知增加
     */
    private Integer perceiveUp;

    /**
     * 智力增加
     */
    private Integer intelligenceUp;

    /**
     * 魅力增加
     */
    private Integer charmUp;

    /**
     * 防御加值 AC
     */
    private Integer armorClassUp;

    /**
     * 伤害加值
     */
    private Integer increasedDamage;

    /**
     * 攻击加值
     */
    private Integer IncreasedAttacks;

    /**
     * 施法者等级增加
     */
    private Integer casterLevelUp;

}
