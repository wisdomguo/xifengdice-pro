package com.wisdom.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdom.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BattleRoom
 * 对战房间
 * @author wisdom-guo
 * @since 2021/3/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("battle_room")
public class BattleRoom extends BaseEntity {

    private Long roomUserId;

    private Long waitUserId;

    private Integer rule;

    private Integer type;

}
