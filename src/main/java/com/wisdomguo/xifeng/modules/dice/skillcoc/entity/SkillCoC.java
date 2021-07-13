package com.wisdomguo.xifeng.modules.dice.skillcoc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SkillCoC
 * coc人物卡技能
 * @author wisdom-guo
 * @since 2021/4/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("skill_coc")
public class SkillCoC  implements Serializable {
    @TableId
    private Long id;

    private Long characterId;

    private String name;

    private Integer value;

    public SkillCoC(Long characterId,String name,Integer value){
        this.characterId=characterId;
        this.name=name;
        this.value=value;
    }
}
