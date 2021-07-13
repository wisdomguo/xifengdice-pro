package com.wisdomguo.xifeng.modules.qqgroup.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * @author wisdomguo
 * @class QQ群组设置
 * @use
 */
@TableName("qqgroup")
public class QQGroup implements Serializable {
    /**
     * 群组ID
     */
    @TableId
    private String groupId;
    /**
     * 骰子开关
     */
    private int diceOpen;
    /**
     * 惜风开关
     */
    private int xfOpen;
    /**
     * 每日开关
     */
    private int otherOpen;

    /**
     * 娱乐开关
     */
    private int gameOpen;

    /**
     *
     */
    private int groupHelpOpen;
}
