package com.wisdomguo.xifeng.modules.blacklist.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * BlackList
 *
 * @author wisdom-guo
 * @since 2021/4/8
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("black_list")
public class BlackList implements Serializable {

    @TableId
    private Long id;

    private Long qgId;

    private int type;

    private String reason;

    private Date createTime;

    private Date updateTime;

    private int isDelete;

    private int foreverDelete;
}
