package com.wisdom.xifeng.service.charactercoc.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wisdom.xifeng.dao.CharacterCocMaper;
import com.wisdom.xifeng.dao.SkillCocMapper;
import com.wisdom.xifeng.entity.CharacterCoC;
import com.wisdom.xifeng.entity.SkillCoC;
import com.wisdom.xifeng.service.charactercoc.CharacterCocService;
import com.wisdom.xifeng.service.skillcoc.SkillCocService;
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
