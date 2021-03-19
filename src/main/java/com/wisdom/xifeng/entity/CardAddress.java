package com.wisdom.xifeng.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
* @author wisdomguo
* @class QQ群组设置
* @use
*/
public class CardAddress {
    //角色卡ID
    private int cardid;
    //角色卡名称
    private String cardname;
    //角色卡地址
    private String fileaddress;
    //玩家QQ号
    private String qqid;
}
