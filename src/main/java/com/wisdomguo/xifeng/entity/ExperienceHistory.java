package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ExperienceHistory
 * 经验获取表
 * @author wisdom-guo
 * @since 2021/3/29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("experience_history")
public class ExperienceHistory extends BaseEntity {

    
    private Long attrId;


    private Date createtime;
}
