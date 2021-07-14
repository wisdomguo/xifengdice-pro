package com.wisdomguo.xifeng.modules.game.fruit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Fruit
 * 果实背包
 * @author wisdom-guo
 * @since 2021/7/8
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("fruit")
public class Fruit implements Serializable {

    @TableId
    private Long id;

    private Long qqId;

    private Integer speciesId;

    private Integer count;

    public Fruit(Long qqId,Integer speciesId,Integer count){
        this.qqId=qqId;
        this.speciesId=speciesId;
        this.count=count;
    }

}
