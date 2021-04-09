package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ArmsRelation
 * 装备关系表
 * @author wisdom-guo
 * @since 2021/3/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment_relation")
public class EquipmentRelation extends BaseEntity {


    /**
     * 人物id
     * */
    private  Long attrId;

    /**
     * 装备id
     * */
    private Long equipmentId;

    /**
     *装备类型(武器/装备)
     * */
    private Integer type;

    /**
     * 装备状态
     * 装备/卸下
     */
    private Integer status;
}
