package com.wisdomguo.xifeng.modules.game.seedbag.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SeedBag
 * 种子袋
 * @author wisdom-guo
 * @since 2021/7/8
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("seed_bag")
public class SeedBag implements Serializable {

    @TableId
    private Long id;
    /**
     * QQID
     */
    private Long qqId;

    /**
     * 种子序号
     */
    private Integer speciesId;

    /**
     * 种子类型
     */
    private Integer type;

    /**
     * 种子数量
     */
    private Integer count;

    public SeedBag(Long qqId,Integer speciesId,Integer type,Integer count){
        this.qqId=qqId;
        this.speciesId=speciesId;
        this.type=type;
        this.count=count;
    }
}
