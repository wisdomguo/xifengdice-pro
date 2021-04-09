package com.wisdomguo.xifeng.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wisdomguo.xifeng.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProfessionRelation
 * 职业关系实体类
 * @author wisdom-guo
 * @since 2021/3/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("profession_relation")
public class ProfessionRelation extends BaseEntity {

    /**
     * id
     * */
    @TableId
    private Long id;

    /**
     * 人物模型
     * */
    private Long attrId;

    /**
     * 职业列表
     * */
    private Long professionId;

    /**
     * 主要职业
     * */
    private Integer mainProfession;

}
