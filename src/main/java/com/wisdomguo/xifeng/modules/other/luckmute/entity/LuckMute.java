package com.wisdomguo.xifeng.modules.other.luckmute.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * LuckMute
 *
 * @author wisdom-guo
 * @since 2021/5/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("luck_mute")
public class LuckMute implements Serializable {
    @TableId
    private Long id;

    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 被禁言qqid
     */
    private Long qqId;

    /**
     * 禁言时间(min)
     */
    private Integer silenceTime;

    /**
     * 保护时间(min)
     */
    private Integer intervalTime;

    /**
     * 禁言概率
     */
    private Integer muteProbability;

    /**
     * 是否开启
     */
    private Integer open;

    /**
     * 保护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date intervaling;
}
