package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AttrModel
 * 人物模型实体类
 * @author wisdom-guo
 * @since 2021/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("attrmodel")
public class AttrModel extends BaseEntity {

    /**
     * 力量
     */
    private Integer power;

    /**
     * 敏捷
     */
    private Integer agile;


    /**
     * 体质
     */
    private Integer constitution;

    /**
     * 感知
     */
    private Integer perceive;

    /**
     * 智力
     */
    private Integer intelligence;

    /**
     * 魅力
     */
    private Integer charm;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 经验
     */
    private Integer experience;

    /**
     * 生命 HP
     */
    private Integer healthPoint;

    /**
     * 强韧
     */
    private Integer tough;

    /**
     * 反射
     */
    private Integer reflection;

    /**
     * 意志
     */
    private Integer will;

    /**
     * 近战
     */
    private Integer melee;

    /**
     * 远程
     */
    private Integer remotely;

    /**
     * 防御加值 AC
     */
    private Integer armorClass;

    /**
     * 种族ID
     */
    private Long ethnicityId;

    /**
     * qqId
     */
    private Long qqId;

}
