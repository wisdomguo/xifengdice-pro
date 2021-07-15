package com.wisdomguo.xifeng.modules.game.farm.plantedfield.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * PlantedField
 * 田地
 * @author wisdom-guo
 * @since 2021/7/8
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("planted_field")
public class PlantedField implements Serializable {

    @TableId
    private Long id;

    private Long qqId;

    /**
     * 种子序号
     */
    private Integer serial;

    /**
     * 种子类型
     */
    private Integer type;

    /**
     * 阶段
     * 0成长期,1成熟期,-1已采摘
     */
    private Integer stage;

    /**
     * 种植时间
     */
    private Date plantingTime;

    /**
     * 采摘次数
     */
    private Integer times;

    @TableLogic
    private Integer delFlag;

    public PlantedField(Long qqId,Integer serial,Integer type, Integer stage,Date plantingTime,Integer times,Integer delFlag){
        this.qqId=qqId;
        this.serial=serial;
        this.type=type;
        this.stage=stage;
        this.plantingTime=plantingTime;
        this.times=times;
        this.delFlag=delFlag;
    }
}
