package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Profession
 * 职业实体类
 * @author wisdom-guo
 * @since 2021/3/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("profession")
public class Profession extends BaseEntity {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 职业名称
     * */
    private String name;

    /**
     * 职业等级
     * */
    private String professionLevel;

    /**
     * bab等级
     */
    private Integer babLevel;

    /**
     * 强韧等级
     */
    private Integer toughLevel;

    /**
     * 反射等级
     */
    private Integer reflectionLevel;

    /**
     * 意志等级
     */
    private Integer willLevel;

    /**
     * 生命骰
     */
    private Integer life;

    /**
     * 法术属性
     */
    private Integer spellAttributes;

    /**
     * 法术属性DC
     */
    private Integer spellAttributesDC;

    /**
     * 武器擅长
     */
    private String weaponGood;

    /**
     * 防具擅长
     */
    private String armorGood;

    /**
     * 成长能力
     */
    private String growingAbility;

    /**
     * 条件能力
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
