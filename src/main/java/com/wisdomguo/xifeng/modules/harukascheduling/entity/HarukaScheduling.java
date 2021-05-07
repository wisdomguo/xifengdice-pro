package com.wisdomguo.xifeng.modules.harukascheduling.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * HarukaScheduling
 * 排班
 * @author wisdom-guo
 * @since 2021/5/6
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("haruka_scheduling")
public class HarukaScheduling implements Serializable {

    @TableId
    private Long id;

    /**
     * 日期
     */
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date date;

    /**
     * 周一写手
     */
    private Long mon;

    /**
     * 周二写手
     */
    private Long tue;

    /**
     * 周三写手
     */
    private Long wed;

    /**
     * 周四写手
     */
    private Long thu;

    /**
     * 周五写手
     */
    private Long fri;

    /**
     * 周六写手
     */
    private Long sat;

    /**
     * 周日写手
     */
    private Long sun;

    /**
     * 素材写手
     */
    private Long material;

    /**
     * 动态写手
     */
    private Long dynamic;

    /**
     * 排版
     */
    private Long typeSetting;

    /**
     * 审核
     */
    private Long toExamine;



}
