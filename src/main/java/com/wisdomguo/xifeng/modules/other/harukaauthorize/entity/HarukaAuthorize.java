package com.wisdomguo.xifeng.modules.other.harukaauthorize.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * HarukaScheduling
 * 排班
 * @author wisdom-guo
 * @since 2021/5/6
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("haruka_authorize")
public class HarukaAuthorize implements Serializable {

    @TableId
    private Long id;

    private String name;


}
