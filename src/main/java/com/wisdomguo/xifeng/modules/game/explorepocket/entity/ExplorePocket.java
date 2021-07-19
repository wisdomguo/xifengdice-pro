package com.wisdomguo.xifeng.modules.game.explorepocket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ExplorePocket
 * 探索口袋
 * @author wisdom-guo
 * @since 2021/7/8
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("explore_pocket")
public class ExplorePocket implements Serializable {

    @TableId
    private Long qqId;

    /**
     * 星屑
     */
    private  Integer stardust;

    /**
     * 星尘碎片
     */
    private  Integer starFragment;

    /**
     * 星尘
     */
    private  Integer stars;

    /**
     * 用户名
     */
    private String nickName;

    /**
     * 星尘
     */
    private  Integer noviceGift;
}
