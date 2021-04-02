package com.wisdom.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("cardaddress")
/**
 * @author wisdomguo
 * @class QQ群组设置
 * @use
 */
public class CardAddress implements Serializable {
    /**
     * 角色卡ID
     */
    @TableId
    private int cardId;
    /**
     * 角色卡名称
     */
    private String cardName;
    /**
     * 角色卡地址
     */
    private String fileAddress;
    /**
     * 玩家QQ号
     */
    private String qqId;
}
