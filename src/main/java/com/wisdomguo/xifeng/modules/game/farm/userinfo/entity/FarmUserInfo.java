package com.wisdomguo.xifeng.modules.game.farm.userinfo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * UserInfo
 * 个人背包
 * @author wisdom-guo
 * @since 2021/7/8
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("farm_user_info")
public class FarmUserInfo implements Serializable {

    @TableId
    private Long qqId;

    /**
     * 田地数量
     */
    private Integer fieldCount;

    /**
     * 灾害侵蚀数量
     */
    private Integer disasterCount;

    /**
     * 加速卡数量
     */
    private Integer quickenCount;

    /**
     * 偷菜次数
     */
    private Integer stealCount;

    /**
     * 保护时间
     */
    private LocalDateTime protectionTime;

}
