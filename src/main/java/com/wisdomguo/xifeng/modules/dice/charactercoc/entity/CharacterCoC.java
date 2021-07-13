package com.wisdomguo.xifeng.modules.dice.charactercoc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Character
 * COC人物卡
 * @author wisdom-guo
 * @since 2021/4/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("character_coc")
public class CharacterCoC implements Serializable {
    @TableId
    private Long id;
    /**
     * qqid
     */
    private Long qqId;
    /**
     * 组id
     */
    private Long groupId;
    /**
     * 姓名
     */
    private String name;

    /**
     * 力量
     */
    private Integer str;
    /**
     * 敏捷
     */
    private Integer dex;
    /**
     * 意志
     */
    private Integer pow;
    /**
     * 外貌
     */
    private Integer app;
    /**
     * 教育
     */
    private Integer edu;
    /**
     * 体型
     */
    private Integer siz;
    /**
     * 智力
     */
    private Integer ins;
    /**
     * san值
     */
    private Integer san;
    /**
     * 生命
     */
    private Integer hp;
    /**
     * 魔法
     */
    private Integer mp;
    /**
     * 幸运
     */
    private Integer luck;
    /**
     * 默认卡
     */
    private Integer def;
    /**
     * 卡序列
     */
    private Integer count;
}
