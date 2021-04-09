package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Ethnicity
 * 种族实体类
 * @author wisdom-guo
 * @since 2021/3/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ethnicity")
public class Ethnicity extends BaseEntity {

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
     * 体型
     */
    private Integer bodyType;

    /**
     * 标准速度
     */
    private Integer standardSpeed;

    /**
     * 种族语言
     */
    private String ethnicityLanguage;

    /**
     * 额外语言
     */
    private String additionalLanguage;

    /**
     * 天赋职业
     */
    private String talentedProfession;

    /**
     * 调整等级
     */
    private Integer adjustmentLevel;

    /**
     * 天生防御
     */
    private Integer bornAC;

    /**
     * 种类
     */
    private String species;

    /**
     * 武器擅长
     */
    private String weaponGood;

    /**
     * 武器熟悉
     */
    private String weaponsFamiliar;

    /**
     * 种族能力[视觉]
     */
    private String visualAbility;

    /**
     * 种族能力[法术]
     */
    private String spellAbility;

    /**
     * 种族能力[条件]
     */
    private String conditionAbility;

    /**
     * 种族能力[通用]
     */
    private String universalAbility;

    /**
     * 种族能力[特殊]
     */
    private String specialAbility;

}
