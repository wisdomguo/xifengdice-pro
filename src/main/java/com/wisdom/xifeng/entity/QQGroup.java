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
public class QQGroup {
    //群组ID
    private String groupID;
    //骰子开关
    private int diceOpen;
    //惜风开关
    private int xfOpen;
    //其他开关
    private int otherOpen;
}
