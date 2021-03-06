package com.wisdomguo.xifeng.modules.dice.charactercoc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdomguo.xifeng.modules.dice.charactercoc.dao.CharacterCocMaper;
import com.wisdomguo.xifeng.modules.dice.charactercoc.entity.CharacterCoC;
import com.wisdomguo.xifeng.modules.dice.skillcoc.entity.SkillCoC;
import com.wisdomguo.xifeng.modules.dice.charactercoc.service.CharacterCocService;
import com.wisdomguo.xifeng.modules.dice.skillcoc.service.SkillCocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CharacterCocServiceImpl
 *
 * @author wisdom-guo
 * @since 2021/4/3
 */
@Service
public class CharacterCocServiceImpl extends ServiceImpl<CharacterCocMaper, CharacterCoC> implements CharacterCocService {

    @Autowired
    private SkillCocService skillCocService;

    @Override
    public boolean insertCharacterCocAndSkill(CharacterCoC characterCoC, List<SkillCoC> skillCoCs) {
        if(baseMapper.insert(characterCoC)>0){
            if(skillCocService.saveBatch(skillCoCs)){
                return true;
            }
        }
        return false;
    }
}
